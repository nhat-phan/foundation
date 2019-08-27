package net.ntworld.foundation.generator

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.setting.AggregateFactorySettings
import net.ntworld.foundation.generator.setting.EventSettings

@Serializable
data class GeneratorSettings(
    val description: String = "",
    val provider: String,
    val aggregateFactories: List<AggregateFactorySettings>,
    val events: List<EventSettings>
)