package net.ntworld.foundation.cqrs

import net.ntworld.foundation.*

open class InfrastructureQueryHandlerContext(private val self: Infrastructure) {
    @InfrastructureDsl.QueryHandlerDsl
    fun environment(): Environment = self.environment()

    @InfrastructureDsl.QueryHandlerDsl
    fun queryBus(): QueryBus = self.queryBus()

    @InfrastructureDsl.QueryHandlerDsl
    fun eventBus(): EventBus = self.eventBus()
}
