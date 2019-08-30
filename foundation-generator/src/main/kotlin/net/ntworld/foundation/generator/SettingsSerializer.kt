package net.ntworld.foundation.generator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import net.ntworld.foundation.generator.setting.EventSourcedSetting

object SettingsSerializer {
    private val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

    fun serialize(data: GeneratorSettings): String {
        return json.stringify(GeneratorSettings.serializer(), data)
    }

    fun serialize(data: EventSourcedSetting): String {
        return json.stringify(EventSourcedSetting.serializer(), data)
    }

    fun serialize(data: List<EventSourcedSetting>): String {
        return json.stringify(EventSourcedSetting.serializer().list, data)
    }

    fun parseEventSettings(input: String): EventSourcedSetting {
        return json.parse(EventSourcedSetting.serializer(), input)
    }

    fun parse(input: String): GeneratorSettings {
        return json.parse(GeneratorSettings.serializer(), input)
    }

    fun parseEventSettingsCollection(input: String): List<EventSourcedSetting> {
        return json.parse(EventSourcedSetting.serializer().list, input)
    }
}