package com.renzien.phantomim.security.local

import android.content.Context
import android.util.AtomicFile
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.File
import java.io.IOException
import java.security.MessageDigest

class LocalKeyStorage(
    context: Context,
    ownerUid: String
) {
    private val appContext = context.applicationContext
    private val cipher = LocalKeyCipher(ownerUid)
    private val accountFolderName = hashName(ownerUid)

    suspend fun write(
        recordId: String,
        plaintext: ByteArray
    ): Unit = withContext(Dispatchers.IO) {
        require(plaintext.size <= MAX_PLAINTEXT_BYTES) {
            "Local key data is too large."
        }

        storageMutex.withLock {
            val directory = accountDirectory()

            if (!directory.isDirectory && !directory.mkdirs()) {
                throw IOException("Unable to create local key directory.")
            }

            val file = recordFile(directory, recordId)

            val existingFiles = directory.list()
                ?: throw IOException("Unable to inspect local key directory.")

            val encrypted = cipher.encrypt(
                recordId = recordId,
                plaintext = plaintext,
                createKeyIfMissing = existingFiles.isEmpty()
            )

            currentCoroutineContext().ensureActive()

            val output = file.startWrite()
            var committed = false

            try {
                output.write(encrypted)
                output.fd.sync()

                currentCoroutineContext().ensureActive()

                file.finishWrite(output)
                committed = true
            } finally {
                if (!committed) {
                    file.failWrite(output)
                }
            }

            if (!readEncrypted(file).contentEquals(encrypted)) {
                throw IOException("Encrypted file was not committed correctly.")
            }
        }
    }

    suspend fun read(
        recordId: String
    ): ByteArray = withContext(Dispatchers.IO) {
        storageMutex.withLock {
            val file = recordFile(accountDirectory(), recordId)
            val encrypted = readEncrypted(file)

            cipher.decrypt(recordId, encrypted)
        }
    }

    private fun accountDirectory(): File {
        val root = File(appContext.noBackupFilesDir, "local_keys_v1")

        return File(root, accountFolderName)
    }

    private fun recordFile(
        directory: File,
        recordId: String
    ): AtomicFile {
        require(recordId.isNotBlank()) {
            "Record ID must not be blank."
        }

        return AtomicFile(
            File(directory, "${hashName(recordId)}.bin")
        )
    }

    private fun readEncrypted(
        file: AtomicFile
    ): ByteArray {
        return file.openRead().use { input ->
            val size = input.channel.size()

            if (size <= 0L || size > MAX_FILE_BYTES.toLong()) {
                throw IOException("Invalid encrypted file size.")
            }

            val bytes = ByteArray(size.toInt())

            DataInputStream(input).readFully(bytes)

            if (input.read() != -1) {
                throw IOException("Encrypted file changed while reading.")
            }

            bytes
        }
    }

    private fun hashName(
        value: String
    ): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))

        return Base64.encodeToString(
            digest,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
    }

    private companion object {
        const val MAX_PLAINTEXT_BYTES = 1024 * 1024

        // Version 1 adds a version byte, a 12-byte IV and a 16-byte tag.
        const val MAX_FILE_BYTES = MAX_PLAINTEXT_BYTES + 29

        val storageMutex = Mutex()
    }
}