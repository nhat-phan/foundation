package net.ntworld.foundation.generator.type

import kotlinx.serialization.Serializable

@Serializable
data class KotlinMetadata(
    val kind: Int?,
    val packageName: String?,
    val metadataVersion: List<Int>?,
    val bytecodeVersion: List<Int>?,
    val data1: List<String>?,
    val data2: List<String>?,
    val extraString: String?,
    val extraInt: Int?
)
