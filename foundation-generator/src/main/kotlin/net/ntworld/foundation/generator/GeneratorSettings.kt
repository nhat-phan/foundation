package net.ntworld.foundation.generator

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import net.ntworld.foundation.generator.setting.*
import net.ntworld.foundation.generator.type.AnnotationProcessorRunInfo

@Serializable
data class GeneratorSettings(
    val description: String = "",
    val processorVersion: String = "",
    val globalDirectory: String,
    val annotationProcessorRunInfo: List<AnnotationProcessorRunInfo>,
    val aggregateFactories: List<AggregateFactorySetting>,
    val eventSourcings: List<EventSourcingSetting>,
    val eventHandlers: List<EventHandlerSetting>,
    val commandHandlers: List<CommandHandlerSetting>,
    val queryHandlers: List<QueryHandlerSetting>,
    val requestHandlers: List<RequestHandlerSetting>,
    val implementations: List<ImplementationSetting>,
    val messages: List<MessageSetting>,
    val contracts: List<ContractSetting>,
    val fakedAnnotations: List<FakedAnnotationSetting>,
    val fakedProperties: Map<String, FakedPropertySetting>
) {
    fun toMutable(): MutableGeneratorSettings = MutableGeneratorSettings(this)

    companion object {
        private val defaultJson = Json(JsonConfiguration.Stable)
        private val devJson = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

        private fun getJson(isDev: Boolean = false): Json {
            return if (isDev) devJson else defaultJson
        }

        fun stringify(data: GeneratorSettings, isDev: Boolean = false): String {
            return getJson(isDev).stringify(serializer(), data)
        }

        fun parse(input: String, isDev: Boolean = false): GeneratorSettings {
            return getJson(isDev).parse(serializer(), input)
        }
    }
}