package net.ntworld.foundation

interface LocalBusResolver<T, R> {
    fun resolve(instance: T): R?
}