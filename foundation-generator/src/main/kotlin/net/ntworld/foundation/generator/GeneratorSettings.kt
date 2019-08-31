package net.ntworld.foundation.generator

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.setting.AggregateFactorySetting
import net.ntworld.foundation.generator.setting.EventHandlerSetting
import net.ntworld.foundation.generator.setting.EventSourcingSetting

@Serializable
data class GeneratorSettings(
    val description: String = "",
    val provider: String,
    val aggregateFactories: List<AggregateFactorySetting>,
    val events: List<EventSourcingSetting>,
    val eventHandlers: List<EventHandlerSetting>
)