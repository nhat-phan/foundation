package net.ntworld.foundation.internal

import net.ntworld.foundation.*
import net.ntworld.foundation.exception.RequestHandlerNotFoundException

internal class ComposedServiceBus(vararg bus: ResolvableServiceBus) : ResolvableServiceBus {
    private val buses = bus

    @Suppress("UNCHECKED_CAST")
    override fun <R : Response> process(request: Request<R>): ServiceBusProcessResult<R> {
        val handler = this.resolve(request)
        if (null !== handler) {
            return ServiceBusProcessResult.make(handler.execute(request = request, message = null) as R)
        }
        throw RequestHandlerNotFoundException(request.toString())
    }

    override fun resolve(instance: Request<*>): RequestHandler<*, *>? {
        for (bus in buses) {
            val handler = bus.resolve(instance)
            if (null !== handler) {
                return handler
            }
        }
        return null
    }

}