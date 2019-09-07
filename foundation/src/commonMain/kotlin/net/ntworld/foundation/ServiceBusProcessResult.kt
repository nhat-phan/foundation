package net.ntworld.foundation

import net.ntworld.foundation.internal.ServiceBusProcessResultImpl

interface ServiceBusProcessResult<R : Response> {
    fun hasError(): Boolean

    fun getResponse(): R

    infix fun ifError(block: (response: R) -> Unit): R

    companion object {
        fun <R : Response> make(response: R): ServiceBusProcessResult<R> = ServiceBusProcessResultImpl(response)
    }
}
