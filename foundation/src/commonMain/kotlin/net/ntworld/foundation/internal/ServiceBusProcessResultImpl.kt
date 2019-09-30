package net.ntworld.foundation.internal

import net.ntworld.foundation.Response
import net.ntworld.foundation.ServiceBusProcessResult

internal class ServiceBusProcessResultImpl<R : Response>(private val response: R) : ServiceBusProcessResult<R> {
    override fun hasError(): Boolean {
        return null !== response.error
    }

    override fun getResponse(): R {
        return response
    }

    override fun ifError(block: (response: R) -> Unit): R {
        if (hasError()) {
            block.invoke(response)
        }
        return response
    }
}