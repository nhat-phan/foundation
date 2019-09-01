package net.ntworld.foundation.generator

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.setting.*

@Serializable
data class GeneratorSettings(
    val description: String = "",
    val globalDirectory: String,
    val aggregateFactories: List<AggregateFactorySetting>,
    val events: List<EventSourcingSetting>,
    val eventHandlers: List<EventHandlerSetting>,
    val commandHandlers: List<CommandHandlerSetting>,
    val queryHandlers: List<QueryHandlerSetting>
)