package net.ntworld.foundation

interface RequestHandler<in T, out R : Response> where T : Request<out R> {
    fun handle(request: T): R

    @Suppress("UNCHECKED_CAST")
    fun execute(request: Request<*>, message: Message?): R = handle(request as T)
}
