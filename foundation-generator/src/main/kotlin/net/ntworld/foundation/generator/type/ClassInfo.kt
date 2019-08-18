package net.ntworld.foundation.generator.type

import kotlinx.serialization.Serializable

@Serializable
data class ClassInfo(val className: String, val packageName: String)
