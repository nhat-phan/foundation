package net.ntworld.foundation.internal

import net.ntworld.foundation.Message

internal data class MessageImpl(
    override val id: String?,
    override val type: String?,
    override val body: String,
    override val attributes: Map<String, String>
) : Message