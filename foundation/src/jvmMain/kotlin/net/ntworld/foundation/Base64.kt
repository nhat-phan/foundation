package net.ntworld.foundation

actual object Base64 {
    actual fun encode(bytes: ByteArray): String {
        return java.util.Base64.getEncoder().encodeToString(bytes)
    }

    actual fun decode(src: String): ByteArray {
        return java.util.Base64.getDecoder().decode(src)
    }
}
