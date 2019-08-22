package net.ntworld.foundation.util

import kotlinx.serialization.Serializable

@Serializable
data class EncryptedData(
    val type: String,
    val value: String
)