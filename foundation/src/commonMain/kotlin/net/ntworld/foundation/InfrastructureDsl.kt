package net.ntworld.foundation

annotation class InfrastructureDsl {
    @DslMarker
    annotation class CommandHandlerDsl

    @DslMarker
    annotation class QueryHandlerDsl

    @DslMarker
    annotation class GenericDsl
}
