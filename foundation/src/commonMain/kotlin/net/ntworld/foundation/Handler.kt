package net.ntworld.foundation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class Handler(
    val type: Handler.Type = Type.Auto,
    val input: KClass<*> = Any::class,
    val factory: Boolean = false,
    val version: Int = 0
) {
    enum class Type {
        Auto,
        Event,
        Command,
        Query,
        Request
    }
}
