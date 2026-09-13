package com.renzien.phantomim.security.local

import android.util.AtomicFile
import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.MessageDigest
import java.util.UUID
import javax.crypto.AEADBadTagException

@RunWith(AndroidJUnit4::class)
class LocalKeyStorageTest {

    private val context =
        InstrumentationRegistry.getInstrumentation().targetContext

    private val ownerUid = "local-storage-test-${UUID.randomUUID()}"
    private val otherOwnerUid = "local-storage-test-${UUID.randomUUID()}"

    private val recordId = "test-identity"

    private val testData =
        "Dummy private key material".toByteArray(Charsets.UTF_8)

    @Test
    fun encryptedFileCanBeReadByANewInstance() = runBlocking {
        val storage = LocalKeyStorage(context, ownerUid)

        storage.write(recordId, testData)

        val file = recordFile(ownerUid, recordId)

        assertTrue(file.isFile)

        val encryptedBytes = file.readBytes()

        assertArrayEquals(
            testData,
            LocalKeyCipher(ownerUid).decrypt(recordId, encryptedBytes)
        )

        val reopenedStorage = LocalKeyStorage(context, ownerUid)

        assertArrayEquals(
            testData,
            reopenedStorage.read(recordId)
        )
    }

    @Test
    fun updatingOneRecordPreservesAnotherRecord() = runBlocking {
        val storage = LocalKeyStorage(context, ownerUid)

        val otherData = "Other key data".toByteArray(Charsets.UTF_8)
        val updatedData = "Updated key data".toByteArray(Charsets.UTF_8)

        storage.write(recordId, testData)
        storage.write("other-record", otherData)

        storage.write(recordId, updatedData)

        assertArrayEquals(
            updatedData,
            storage.read(recordId)
        )

        assertArrayEquals(
            otherData,
            storage.read("other-record")
        )
    }

    @Test
    fun accountsAreSeparatedAndCopiedFilesAreRejected() = runBlocking {
        val ownerStorage = LocalKeyStorage(context, ownerUid)
        val otherStorage = LocalKeyStorage(context, otherOwnerUid)

        val otherData =
            "Other account key".toByteArray(Charsets.UTF_8)

        ownerStorage.write(recordId, testData)
        otherStorage.write(recordId, otherData)

        assertArrayEquals(testData, ownerStorage.read(recordId))
        assertArrayEquals(otherData, otherStorage.read(recordId))

        recordFile(ownerUid, recordId).copyTo(
            target = recordFile(otherOwnerUid, recordId),
            overwrite = true
        )

        expectFailure(AEADBadTagException::class.java) {
            otherStorage.read(recordId)
        }
    }

    @Test
    fun modifiedFileIsRejectedWithoutBeingReplaced() = runBlocking {
        val storage = LocalKeyStorage(context, ownerUid)

        storage.write(recordId, testData)

        val file = recordFile(ownerUid, recordId)
        val modified = file.readBytes()

        modified[modified.lastIndex] =
            (modified.last().toInt() xor 1).toByte()

        file.writeBytes(modified)

        expectFailure(AEADBadTagException::class.java) {
            storage.read(recordId)
        }

        assertArrayEquals(modified, file.readBytes())
    }

    @Test
    fun missingKeyBlocksReadsAndWritesWithoutReplacingData() = runBlocking {
        val storage = LocalKeyStorage(context, ownerUid)

        storage.write(recordId, testData)

        val file = recordFile(ownerUid, recordId)
        val originalFileBytes = file.readBytes()

        val keyStore = openKeyStore()
        val alias = aliasForTestOwner(ownerUid)

        assertTrue(keyStore.containsAlias(alias))

        keyStore.deleteEntry(alias)

        expectFailure(GeneralSecurityException::class.java) {
            storage.read(recordId)
        }

        expectFailure(GeneralSecurityException::class.java) {
            storage.write(recordId, testData)
        }

        expectFailure(GeneralSecurityException::class.java) {
            storage.write("new-record", testData)
        }

        assertFalse(keyStore.containsAlias(alias))
        assertFalse(recordFile(ownerUid, "new-record").exists())

        assertArrayEquals(
            originalFileBytes,
            file.readBytes()
        )
    }

    @Test
    fun oversizedWritePreservesExistingData() = runBlocking {
        val storage = LocalKeyStorage(context, ownerUid)

        storage.write(recordId, testData)

        val file = recordFile(ownerUid, recordId)
        val originalFileBytes = file.readBytes()

        expectFailure(IllegalArgumentException::class.java) {
            storage.write(
                recordId = recordId,
                plaintext = ByteArray(1024 * 1024 + 1)
            )
        }

        assertArrayEquals(originalFileBytes, file.readBytes())
        assertArrayEquals(testData, storage.read(recordId))
    }

    @After
    fun removeTestFilesAndKeys() {
        val keyStore = openKeyStore()

        listOf(ownerUid, otherOwnerUid).forEach { uid ->
            keyStore.deleteEntry(aliasForTestOwner(uid))

            listOf(recordId, "other-record", "new-record").forEach { id ->
                AtomicFile(recordFile(uid, id)).delete()
            }

            val directory = accountDirectory(uid)

            if (directory.exists()) {
                assertTrue(
                    "Unable to remove test directory.",
                    directory.delete()
                )
            }
        }
    }

    private fun accountDirectory(uid: String): File {
        val root = File(context.noBackupFilesDir, "local_keys_v1")

        return File(root, hashName(uid))
    }

    private fun recordFile(
        uid: String,
        recordId: String
    ): File {
        return File(
            accountDirectory(uid),
            "${hashName(recordId)}.bin"
        )
    }

    private fun hashName(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))

        return encode(digest)
    }

    private fun aliasForTestOwner(uid: String): String {
        return "phantom_im_local_v1_" +
                encode(uid.toByteArray(Charsets.UTF_8))
    }

    private fun encode(bytes: ByteArray): String {
        return Base64.encodeToString(
            bytes,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
    }

    private fun openKeyStore(): KeyStore {
        return KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
    }

    private suspend fun expectFailure(
        type: Class<out Exception>,
        action: suspend () -> Unit
    ) {
        try {
            action()
            fail("Expected ${type.simpleName}.")
        } catch (error: Exception) {
            if (!type.isInstance(error)) {
                throw error
            }
        }
    }
}