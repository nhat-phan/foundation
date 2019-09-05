package net.ntworld.foundation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class Implementation(
    val type: Type = Type.Auto,
    val contract: KClass<*> = Any::class
) {
    enum class Type {
        Auto,
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
