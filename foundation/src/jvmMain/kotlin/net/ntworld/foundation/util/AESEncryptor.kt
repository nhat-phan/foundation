package net.ntworld.foundation.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.ntworld.foundation.eventSourcing.Encryptor
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec

class AESEncryptor(
    override val cipherId: String,
    private val secret: String
) : Encryptor {
    override val algorithm: String = "AES"
    private val json = Json(JsonConfiguration.Stable)
    private val encryptor = Cipher.getInstance("AES/ECB/PKCS5Padding")
    private val decryptor = Cipher.getInstance("AES/ECB/PKCS5Padding")

    init {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(secret.toCharArray(), byteArrayOf(), 65536, 256);
        val secretKey = factory.generateSecret(spec)
        val secretKeySpec = SecretKeySpec(secretKey.encoded, algorithm)

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
            // TODO: here
            throw exception
        }
    }
}