package net.ntworld.foundation.generator;

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import java.text.SimpleDateFormat
import java.util.*

internal object Framework {
    val namespace = "net.ntworld.foundation"

    val Infrastructure = ClassName(namespace, "Infrastructure")
    val InfrastructureProvider = ClassName(namespace, "InfrastructureProvider")

    val Faker = ClassName(namespace, "Faker")

    val Message = ClassName(namespace, "Message")
    val MessageConverter = ClassName(namespace, "MessageConverter")

    val AggregateFactory = ClassName(namespace, "AggregateFactory")

    val EventEntity = ClassName("$namespace.eventSourcing", "EventEntity")
    val EventConverter = ClassName("$namespace.eventSourcing", "EventConverter")
    val EventSourcedFactory = ClassName("$namespace.eventSourcing", "EventSourcedFactory")

    val EventEntityConverterUtility = ClassName("$namespace.eventSourcing", "EventEntityConverterUtility")
    val EventEntityConverterUtilitySetting = ClassName("$namespace.eventSourcing.EventEntityConverterUtility", "Setting")

    val FakerRelatedSource_FakedData = "FakedData"
    val FakerRelatedSource_JavaFakerWrapper_Jvm = "JavaFakerWrapper"

    val JavaFaker = ClassName("com.github.javafaker", "Faker")
    val Json = ClassName("kotlinx.serialization.json", "Json")
    val JsonConfiguration = ClassName("kotlinx.serialization.json", "JsonConfiguration")
}