package net.ntworld.foundation

interface ServiceBus {
    infix fun <R : Response<*>> process(request: Request<R>): ServiceBusProcessResult<R>

    fun <R : Response<*>> process(request: Request<R>, responseErrorHandler: (response: R) -> Unit): R {
        return this.process(request).ifError(responseErrorHandler)
    }
}
