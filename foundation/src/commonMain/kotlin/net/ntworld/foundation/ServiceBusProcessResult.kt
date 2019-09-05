package net.ntworld.foundation

interface ServiceBusProcessResult<R : Response<*>> {
    fun hasError(): Boolean

    fun getResponse(): R

    infix fun ifError(block: (response: R) -> Unit): R
}
