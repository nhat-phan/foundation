package net.ntworld.foundation.generator

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import net.ntworld.foundation.generator.setting.EventSettings

object SettingsSerializer {
    private val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

    fun serialize(data: GeneratorSettings): String {
        return json.stringify(GeneratorSettings.serializer(), data)
    }

    fun serialize(data: EventSettings): String {
        return json.stringify(EventSettings.serializer(), data)
    }

    fun serialize(data: List<EventSettings>): String {
        return json.stringify(EventSettings.serializer().list, data)
    }

    fun parseEventSettings(input: String): EventSettings {
        return json.parse(EventSettings.serializer(), input)
    }

    fun parse(input: String): GeneratorSettings {
        return json.parse(GeneratorSettings.serializer(), input)
    }

    fun parseEventSettingsCollection(input: String): List<EventSettings> {
        return json.parse(EventSettings.serializer().list, input)
    }
}