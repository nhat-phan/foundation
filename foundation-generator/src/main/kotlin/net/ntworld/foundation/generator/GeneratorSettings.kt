package net.ntworld.foundation.generator

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.setting.*
import net.ntworld.foundation.generator.type.AnnotationProcessorRunInfo

@Serializable
data class GeneratorSettings(
    val description: String = "",
    val globalDirectory: String,
    val annotationProcessorRunInfo: List<AnnotationProcessorRunInfo>,
    val aggregateFactories: List<AggregateFactorySetting>,
    val eventSourcings: List<EventSourcingSetting>,
    val eventHandlers: List<EventHandlerSetting>,
    val commandHandlers: List<CommandHandlerSetting>,
    val queryHandlers: List<QueryHandlerSetting>,
    val implementations: List<ImplementationSetting>,
    val messages: List<MessageSetting>,
    val contracts: List<ContractSetting>
) {
    fun toMutable(): MutableGeneratorSettings = MutableGeneratorSettings(this)
}