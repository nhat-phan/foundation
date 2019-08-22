package com.example

import com.example.event.TodoCreatedEvent
import com.example.event.generated.TodoCreatedEventConverter
import net.ntworld.foundation.IdGenerator
import net.ntworld.foundation.InfrastructureProvider
import net.ntworld.foundation.eventSourcing.Encryptor
import net.ntworld.foundation.util.AESEncryptor
import net.ntworld.foundation.util.UUIDGenerator
import kotlin.reflect.KClass

class InfrastructureManager: InfrastructureProvider() {
    override fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator {
        return UUIDGenerator
    }

    override fun encryptor(): Encryptor {
        return AESEncryptor("test", "test", "salt")
    }

    override fun encryptor(cipherId: String, algorithm: String): Encryptor {
        return AESEncryptor(cipherId, "tests", "salt")
    }
}

fun main() {
    val event = TodoCreatedEvent(
        id = "1",
        companyId = "1",
        task = "do something",
        money = 1234.567
    )
    val infrastructure = InfrastructureManager()
    val eventData = TodoCreatedEventConverter(infrastructure).toEventData("type", "id", 1, event)

    val readEvent = TodoCreatedEventConverter(infrastructure).fromEventData(eventData)
    println(readEvent)
    println(event == readEvent)
    println(event === readEvent)
}