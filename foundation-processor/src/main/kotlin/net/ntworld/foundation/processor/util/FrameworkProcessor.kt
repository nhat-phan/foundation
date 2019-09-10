package net.ntworld.foundation.processor.util

internal object FrameworkProcessor {
    const val SETTINGS_PATH = "resources"
    const val SETTINGS_FILENAME = "foundation-settings.json"

    const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    const val DEV_OPTION_NAME = "foundation.processor.dev"
    const val MODE_OPTION_NAME = "foundation.processor.mode"
    const val SETTINGS_CLASS_OPTION_NAME = "foundation.processor.settingsClass"

    const val Contract = "net.ntworld.foundation.Contract"
    const val Aggregate = "net.ntworld.foundation.Aggregate"
    const val AbstractEventSourced = "net.ntworld.foundation.eventSourcing.AbstractEventSourced"

    const val Handler = "net.ntworld.foundation.Handler"
    const val Use = "net.ntworld.foundation.Use"
    const val Faked = "net.ntworld.foundation.Faked"
    const val Implementation = "net.ntworld.foundation.Implementation"

    const val EventSourced = "net.ntworld.foundation.eventSourcing.EventSourced"
    const val EventSourcing = "net.ntworld.foundation.eventSourcing.EventSourcing"
    const val EventSourcingMetadata = "net.ntworld.foundation.eventSourcing.EventSourcing.Metadata"
    const val EventSourcingEncrypted = "net.ntworld.foundation.eventSourcing.EventSourcing.Encrypted"

    val SUPPORTED_OPTIONS = setOf(
        KAPT_KOTLIN_GENERATED_OPTION_NAME,
        MODE_OPTION_NAME,
        DEV_OPTION_NAME,
        SETTINGS_CLASS_OPTION_NAME
    )

    val SUPPORTED_ANNOTATION_TYPES = setOf(
        Use,
        Faked,
        Implementation,
        Handler,
        EventSourced,
        EventSourcing,
        EventSourcingMetadata,
        EventSourcingEncrypted
    )
}
