//package com.example
//
//import com.example.event.TodoCreatedEvent
//import com.example.event.generated.TodoCreatedEventConverter
//import net.ntworld.foundation.*
//import net.ntworld.foundation.eventSourcing.Encryptor
//import net.ntworld.foundation.util.AESEncryptor
//import net.ntworld.foundation.util.JavaFakerWrapper
//import net.ntworld.foundation.util.UUIDGenerator
//import kotlin.reflect.KClass
//import com.github.javafaker.Faker as JavaFaker
//
//class InfrastructureManager: InfrastructureProvider() {
//    override fun environment(): Environment {
//        return Environment.Development
//    }
//
//    override fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator {
//        return UUIDGenerator
//    }
//
//    override fun encryptor(): Encryptor {
//        return AESEncryptor("test", "0a384b98-c5d8-11e9-bc11-9ddf084443fe-0a384b98-c5d8-11e9-bc11-9ddf084443fe", "salt")
//    }
//
//    override fun encryptor(cipherId: String, algorithm: String): Encryptor {
//        return AESEncryptor(cipherId, "test", "salt")
//    }
//
//    override fun faker(): Faker {
//        return JavaFakerWrapper(JavaFaker())
//    }
//}
//
//fun mainx() {
//    val event = TodoCreatedEvent(
//        id = "1",
//        companyId = "1",
//        task = "do something very dangerous",
//        money = 1234.567
//    )
//    val infrastructure = InfrastructureManager()
//    val eventEntity = TodoCreatedEventConverter(infrastructure).toEventEntity("type", "id", 1, event)
//    println(eventEntity)
//
//    val readEvent = TodoCreatedEventConverter(infrastructure).fromEventEntity(eventEntity)
//    println(readEvent)
//    println(event == readEvent)
//    println(event === readEvent)
//}