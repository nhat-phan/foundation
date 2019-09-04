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
    val JavaFakerWrapper = ClassName("$namespace.util", "JavaFakerWrapper")

    val LocalBusResolver = ClassName(namespace, "LocalBusResolver")

    val Event = ClassName(namespace, "Event")
    val EventHandler = ClassName(namespace, "EventHandler")
    val EventBus = ClassName(namespace, "EventBus")

    val HandlerVersioningStrategy = ClassName(namespace, "HandlerVersioningStrategy")

    val Command = ClassName("$namespace.cqrs", "Command")
    val CommandHandler = ClassName("$namespace.cqrs", "CommandHandler")
    val CommandBus = ClassName("$namespace.cqrs", "CommandBus")

    val Query = ClassName("$namespace.cqrs", "Query")
    val QueryHandler = ClassName("$namespace.cqrs", "QueryHandler")
    val QueryBus = ClassName("$namespace.cqrs", "QueryBus")

    val QueryHandlerNotFoundException = ClassName("$namespace.exception", "QueryHandlerNotFoundException")

    val Message = ClassName(namespace, "Message")
    val MessageAttribute = ClassName(namespace, "MessageAttribute")
    val MessageUtility = ClassName(namespace, "MessageUtility")
    val MessageTranslator = ClassName(namespace, "MessageTranslator")

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