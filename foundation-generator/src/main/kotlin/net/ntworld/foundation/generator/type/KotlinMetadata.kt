package net.ntworld.foundation.generator.type

import kotlinx.serialization.Serializable

@Serializable
data class KotlinMetadata(
    val kind: Int?,
    val packageName: String?,
    val metadataVersion: Set<Int>?,
    val bytecodeVersion: Set<Int>?,
    val data1: Set<String>?,
    val data2: Set<String>?,
    val extraString: String?,
    val extraInt: Int?
)
