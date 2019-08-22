package net.ntworld.foundation.eventSourcing

interface Encryptor {
    val cipherId: String
    val algorithm: String

    fun encrypt(input: String): String

    fun decrypt(input: String): String
}
