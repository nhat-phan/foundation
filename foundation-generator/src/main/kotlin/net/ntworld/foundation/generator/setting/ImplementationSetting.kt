package net.ntworld.foundation.generator.setting

import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.type.ClassInfo

@Serializable
data class ImplementationSetting(
    val implementation: ClassInfo,
    val contract: ClassInfo,
    val type: Type,
    val isGenerated: Boolean
) : Setting {
    override val name: String = "${implementation.packageName}.${implementation.className}"

    enum class Type {
        Unknown,
        Aggregate,
        Error,
        State,
        ReceivedData,
        Event,
        Command,
        Query,
        QueryResult,
        Request,
        Response
    }
}
