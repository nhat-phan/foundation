package net.ntworld.foundation.generator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import net.ntworld.foundation.generator.setting.EventSourcingSetting

object SettingsSerializer {
    private val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

    fun serialize(data: GeneratorSettings): String {
        return json.stringify(GeneratorSettings.serializer(), data)
    }

    fun serialize(data: EventSourcingSetting): String {
        return json.stringify(EventSourcingSetting.serializer(), data)
    }

    fun serialize(data: List<EventSourcingSetting>): String {
        return json.stringify(EventSourcingSetting.serializer().list, data)
    }

    fun parseEventSettings(input: String): EventSourcingSetting {
        return json.parse(EventSourcingSetting.serializer(), input)
    }

    fun parse(input: String): GeneratorSettings {
        return json.parse(GeneratorSettings.serializer(), input)
    }

    fun parseEventSettingsCollection(input: String): List<EventSourcingSetting> {
        return json.parse(EventSourcingSetting.serializer().list, input)
    }
}