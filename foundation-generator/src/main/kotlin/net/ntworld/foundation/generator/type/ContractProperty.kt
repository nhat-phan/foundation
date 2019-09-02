package net.ntworld.foundation.generator.type

import kotlinx.serialization.Serializable

@Serializable
data class ContractProperty(
    val name: String,
    val order: Int,
    val unknownAnnotations: List<String>,
    val hasNotNullAnnotation: Boolean,
    val hasFakedAnnotation: Boolean,
    val fakedType: String?
)