package net.ntworld.foundation

import net.ntworld.foundation.internal.ComposedServiceBus

interface ServiceBus {
    infix fun <R : Response> process(request: Request<R>): ServiceBusProcessResult<R>

    fun <R : Response> process(request: Request<R>, responseErrorHandler: (response: R) -> Unit): R {
        return this.process(request).ifError(responseErrorHandler)
    }

    companion object {
        fun compose(vararg bus: ResolvableServiceBus): ResolvableServiceBus {
            return ComposedServiceBus(*bus)
        }
    }
}
