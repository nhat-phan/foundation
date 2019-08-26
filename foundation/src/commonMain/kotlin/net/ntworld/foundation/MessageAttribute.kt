package net.ntworld.foundation

import kotlinx.io.ByteBuffer

interface MessageAttribute {
    val dataType: String

    val binaryValue: ByteBuffer?

    val stringValue: String?
}