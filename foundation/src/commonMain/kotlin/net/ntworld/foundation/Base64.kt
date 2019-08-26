package net.ntworld.foundation

expect object Base64 {
    fun encode(bytes: ByteArray): String

    fun decode(src: String): ByteArray
}