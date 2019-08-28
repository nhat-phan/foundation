package net.ntworld.foundation

import net.ntworld.foundation.exception.NotFoundException

interface DataReceiver<T> {
    fun findOrNull(id: String): T?

    fun find(id: String): T {
        val result = findOrNull(id)
        if (null === result) {
            throw NotFoundException()
        }
        return result
    }

    fun findOrFail(id: String): T = find(id)

    fun findOrDefault(id: String, default: T): T {
        val result = findOrNull(id)
        if (null === result) {
            return default
        }
        return result
    }
}