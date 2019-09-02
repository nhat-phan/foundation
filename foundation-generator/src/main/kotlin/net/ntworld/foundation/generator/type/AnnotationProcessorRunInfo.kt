package net.ntworld.foundation.generator.type

import kotlinx.serialization.Serializable

@Serializable
data class AnnotationProcessorRunInfo(
    val annotations: List<String>,
    val startedAt: Long,
    val finishedAt: Long,
    val duration: Long
)