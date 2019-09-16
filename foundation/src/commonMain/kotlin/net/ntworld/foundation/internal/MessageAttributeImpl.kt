package net.ntworld.foundation.internal

import kotlinx.io.ByteBuffer
import net.ntworld.foundation.MessageAttribute

internal data class MessageAttributeImpl(
    override val dataType: String,
    override val binaryValue: ByteBuffer?,
    override val stringValue: String?
) : MessageAttribute