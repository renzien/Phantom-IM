package com.renzien.phantomim.security.local

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.util.UUID
import javax.crypto.AEADBadTagException

@RunWith(AndroidJUnit4::class)
class LocalKeyCipherTest {

    private val ownerUid = "local-key-test-${UUID.randomUUID()}"
    private val otherOwnerUid = "local-key-test-${UUID.randomUUID()}"

    private val recordId = "test-identity"
    private val testData = "Local key test".toByteArray(Charsets.UTF_8)

    @Test
    fun dataCanBeDecryptedByANewInstance() = runBlocking {
        val encrypted = LocalKeyCipher(ownerUid).encrypt(
            recordId,
            testData
        )

        val decrypted = LocalKeyCipher(ownerUid).decrypt(
            recordId,
            encrypted
        )

        assertArrayEquals(testData, decrypted)
    }

    @Test
    fun repeatedEncryptionProducesDifferentResults() = runBlocking {
        val cipher = LocalKeyCipher(ownerUid)

        val first = cipher.encrypt(recordId, testData)
        val second = cipher.encrypt(recordId, testData)

        assertFalse(first.contentEquals(second))

        assertArrayEquals(
            testData,
            cipher.decrypt(recordId, first)
        )

        assertArrayEquals(
            testData,
            cipher.decrypt(recordId, second)
        )
    }

    @Test
    fun modifiedDataIsRejected() = runBlocking {
        val cipher = LocalKeyCipher(ownerUid)
        val encrypted = cipher.encrypt(recordId, testData)

        val modified = encrypted.copyOf()

        modified[modified.lastIndex] =
            (modified.last().toInt() xor 1).toByte()

        expectAuthenticationFailure {
            cipher.decrypt(recordId, modified)
        }
    }

    @Test
    fun differentRecordIdIsRejected() = runBlocking {
        val cipher = LocalKeyCipher(ownerUid)
        val encrypted = cipher.encrypt(recordId, testData)

        expectAuthenticationFailure {
            cipher.decrypt(
                recordId = "different-record",
                encryptedData = encrypted
            )
        }
    }

    @Test
    fun differentAccountIsRejected() = runBlocking {
        val ownerCipher = LocalKeyCipher(ownerUid)
        val otherCipher = LocalKeyCipher(otherOwnerUid)

        val encrypted = ownerCipher.encrypt(recordId, testData)

        val otherEncrypted = otherCipher.encrypt(recordId, testData)

        assertArrayEquals(
            testData,
            otherCipher.decrypt(recordId, otherEncrypted)
        )

        expectAuthenticationFailure {
            otherCipher.decrypt(recordId, encrypted)
        }
    }

    @Test
    fun missingKeyIsNotRecreatedDuringDecryption() = runBlocking {
        val cipher = LocalKeyCipher(ownerUid)
        val encrypted = cipher.encrypt(recordId, testData)

        val keyStore = openKeyStore()
        val alias = aliasForTestOwner(ownerUid)

        assertTrue(keyStore.containsAlias(alias))

        keyStore.deleteEntry(alias)

        try {
            cipher.decrypt(recordId, encrypted)
            fail("Decryption should fail when the key is missing.")
        } catch (_: GeneralSecurityException) {
            // Expected: the original key has been removed.
        }

        assertFalse(keyStore.containsAlias(alias))
    }

    @Test
    fun encryptionDoesNotCreateAKeyWhenCreationIsDisabled() = runBlocking {
        val cipher = LocalKeyCipher(ownerUid)
        val keyStore = openKeyStore()
        val alias = aliasForTestOwner(ownerUid)

        assertFalse(keyStore.containsAlias(alias))

        try {
            cipher.encrypt(
                recordId = recordId,
                plaintext = testData,
                createKeyIfMissing = false
            )

            fail("Encryption should fail when no key exists.")
        } catch (_: GeneralSecurityException) {
            // Expected: creating a replacement key is not allowed.
        }

        assertFalse(keyStore.containsAlias(alias))
    }

    @Test
    fun existingKeyWorksWhenCreationIsDisabled() = runBlocking {
        val cipher = LocalKeyCipher(ownerUid)

        val first = cipher.encrypt(recordId, testData)

        val second = cipher.encrypt(
            recordId = recordId,
            plaintext = testData,
            createKeyIfMissing = false
        )

        assertArrayEquals(
            testData,
            cipher.decrypt(recordId, first)
        )

        assertArrayEquals(
            testData,
            cipher.decrypt(recordId, second)
        )
    }

    @After
    fun removeTestKeys() {
        val keyStore = openKeyStore()

        listOf(ownerUid, otherOwnerUid).forEach { uid ->
            keyStore.deleteEntry(aliasForTestOwner(uid))
        }
    }

    private fun openKeyStore(): KeyStore {
        return KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
    }

    private fun aliasForTestOwner(uid: String): String {
        return "phantom_im_local_v1_" +
                Base64.encodeToString(
                    uid.toByteArray(Charsets.UTF_8),
                    Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
                )
    }

    private suspend fun expectAuthenticationFailure(
        block: suspend () -> Unit
    ) {
        try {
            block()
            fail("Expected authentication to fail.")
        } catch (_: AEADBadTagException) {
            // Expected: the encrypted data or its context does not match.
        }
    }
}