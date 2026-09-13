package com.renzien.phantomim.security.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class LocalKeyCipher(
    ownerUid: String
) {
    init {
        require(ownerUid.isNotBlank()) {
            "Owner UID must not be blank."
        }
    }

    private val keyAlias = "phantom_im_local_v1_" +
            Base64.encodeToString(
                ownerUid.toByteArray(Charsets.UTF_8),
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            )

    suspend fun encrypt(
        recordId: String,
        plaintext: ByteArray,
        createKeyIfMissing: Boolean = true
    ): ByteArray = withContext(Dispatchers.IO) {
        val associatedData = createAssociatedData(recordId)

        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(
            Cipher.ENCRYPT_MODE,
            getKey(createIfMissing = createKeyIfMissing)
        )

        cipher.updateAAD(associatedData)

        val iv = cipher.iv

        check(iv.size == IV_SIZE) {
            "Unexpected IV size."
        }

        val ciphertext = cipher.doFinal(plaintext)

        byteArrayOf(VERSION) + iv + ciphertext
    }

    suspend fun decrypt(
        recordId: String,
        encryptedData: ByteArray
    ): ByteArray = withContext(Dispatchers.IO) {
        val associatedData = createAssociatedData(recordId)
        val headerSize = 1 + IV_SIZE
        val minimumSize = headerSize + TAG_BITS / 8

        if (
            encryptedData.size < minimumSize ||
            encryptedData[0] != VERSION
        ) {
            throw GeneralSecurityException(
                "Invalid local ciphertext."
            )
        }

        val iv = encryptedData.copyOfRange(
            fromIndex = 1,
            toIndex = headerSize
        )

        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(
            Cipher.DECRYPT_MODE,
            getKey(createIfMissing = false),
            GCMParameterSpec(TAG_BITS, iv)
        )

        cipher.updateAAD(associatedData)

        cipher.doFinal(
            encryptedData,
            headerSize,
            encryptedData.size - headerSize
        )
    }

    private fun createAssociatedData(
        recordId: String
    ): ByteArray {
        require(recordId.isNotBlank()) {
            "Record ID must not be blank."
        }

        return "phantom-im:local-key:v1:$keyAlias:$recordId"
            .toByteArray(Charsets.UTF_8)
    }

    private fun getKey(
        createIfMissing: Boolean
    ): SecretKey = synchronized(keyLock) {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
            load(null)
        }

        if (keyStore.containsAlias(keyAlias)) {
            return@synchronized keyStore.getKey(
                keyAlias,
                null
            ) as? SecretKey ?: throw GeneralSecurityException(
                "Invalid local encryption key."
            )
        }

        if (!createIfMissing) {
            throw GeneralSecurityException(
                "Local encryption key is missing."
            )
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val keySpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(256)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(
                KeyProperties.ENCRYPTION_PADDING_NONE
            )
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(keySpec)
        keyGenerator.generateKey()
    }

    private companion object {
        const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val VERSION: Byte = 1
        const val IV_SIZE = 12
        const val TAG_BITS = 128

        val keyLock = Any()
    }
}