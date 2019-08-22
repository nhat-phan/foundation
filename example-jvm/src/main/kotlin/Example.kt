//package com.example
//
//import com.example.event.TodoCreatedEvent
//import com.example.event.generated.TodoCreatedEventConverter
//import net.ntworld.foundation.IdGenerator
//import net.ntworld.foundation.InfrastructureProvider
//import net.ntworld.foundation.util.UUIDGenerator
//import kotlin.reflect.KClass
//
//class InfrastructureManager: InfrastructureProvider() {
//    override fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator {
//        return UUIDGenerator
//    }
//}
//
//fun main() {
//    val event = TodoCreatedEvent(
//        id = "1",
//        companyId = "1",
//        task = "do something"
//    )
//    val infrastructure = InfrastructureManager()
//    val eventData = TodoCreatedEventConverter(infrastructure).toEventData("type", "id", 1, event)
//    println(eventData)
//}