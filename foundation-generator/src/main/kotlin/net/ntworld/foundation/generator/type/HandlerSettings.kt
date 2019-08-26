package net.ntworld.foundation.generator.type

interface HandlerSettings {
    val bus: String

    val handler: ClassInfo

    val isResolvable: Boolean
}