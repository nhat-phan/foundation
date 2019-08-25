package net.ntworld.foundation.util

import net.ntworld.foundation.exception.DecryptException
import net.ntworld.foundation.eventSourcing.Encryptor
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class AESEncryptor(
    override val cipherId: String,
    secret: String,
    salt: String
) : Encryptor {
    override val algorithm: String = "$ALGORITHM/net.ntworld.foundation.util.AESEncryptor"
    private val encryptor = Cipher.getInstance("AES/ECB/PKCS5Padding")
    private val decryptor = Cipher.getInstance("AES/ECB/PKCS5Padding")

    init {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(secret.toCharArray(), salt.toByteArray(), 65536, 256);
        val secretKey = factory.generateSecret(spec)
        val secretKeySpec = SecretKeySpec(secretKey.encoded, ALGORITHM)

        encryptor.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        decryptor.init(Cipher.DECRYPT_MODE, secretKeySpec)
    }

    override fun encrypt(input: String): String {
        return Base64.getEncoder().encodeToString(
            encryptor.doFinal(input.toByteArray())
        )
    }

    override fun decrypt(input: String): String {
        try {
            val bytes = decryptor.doFinal(Base64.getDecoder().decode(input.toByteArray()))

            return String(bytes)
        } catch (exception: Exception) {
            throw DecryptException(exception)
        }
    }

    companion object {
        private const val ALGORITHM = "AES"
    }
}