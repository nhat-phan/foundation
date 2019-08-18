package net.ntworld.foundation.generator

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.setting.EventSettings

@Serializable
data class GeneratorSettings(
    val description: String = "",
    val events: List<EventSettings>
)