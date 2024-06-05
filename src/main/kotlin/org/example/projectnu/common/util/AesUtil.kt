package org.example.projectnu.common.util

import org.apache.commons.codec.binary.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object AesUtil {
    private const val ALGORITHM = "AES/ECB/PKCS5Padding"
    private const val SECRET_KEY_ALGORITHM = "AES"
    private const val CHARSET_NAME = "UTF-8"
    fun encrypt(password: String, email: String): String {
        val secretKey = getSecretKey(email)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(password.toByteArray(charset(CHARSET_NAME)))
        return Base64.encodeBase64String(encryptedBytes)
    }

    fun decrypt(encryptedPassword: String, email: String): String {
        val secretKey = getSecretKey(email)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(Base64.decodeBase64(encryptedPassword))
        return String(decryptedBytes, charset(CHARSET_NAME))
    }

    private fun getSecretKey(email: String): SecretKey {
        val keyBytes = email.toByteArray(charset(CHARSET_NAME))
        val sha = MessageDigest.getInstance("SHA-256")
        val hashedKey = sha.digest(keyBytes)
        return SecretKeySpec(hashedKey.copyOf(16), SECRET_KEY_ALGORITHM)
    }
}
