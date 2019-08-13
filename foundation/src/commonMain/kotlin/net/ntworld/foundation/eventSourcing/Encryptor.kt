package net.ntworld.foundation.eventSourcing

interface Encryptor {
    val cipherId: String
    val algorithm: String

    fun <T : Any> encrypt(value: T): String

    fun <T : Any> decrypt(value: String): T
}
