package net.ntworld.foundation.generator;

import com.squareup.kotlinpoet.ClassName
import net.ntworld.foundation.generator.type.ComponentType

internal object Framework {
    val namespace = "net.ntworld.foundation"

    val Infrastructure = ClassName(namespace, "Infrastructure")
    val InfrastructureProvider = ClassName(namespace, "InfrastructureProvider")

    val Faker = ClassName(namespace, "Faker")
    val JavaFakerWrapper = ClassName("$namespace.util", "JavaFakerWrapper")

    val LocalBusResolver = ClassName(namespace, "LocalBusResolver")

    val Aggregate = ClassName(namespace, "Aggregate")
    val Error = ClassName(namespace, "Error")
    val State = ClassName(namespace, "State")
    val ReceivedData = ClassName(namespace, "ReceivedData")

    val Event = ClassName(namespace, "Event")
    val EventHandler = ClassName(namespace, "EventHandler")
    val EventBus = ClassName(namespace, "EventBus")

    val Request = ClassName(namespace, "Request")
    val Response = ClassName(namespace, "Response")
    val RequestHandler = ClassName(namespace, "RequestHandler")
    val ServiceBus = ClassName(namespace, "ServiceBus")
    val ServiceBusProcessResult = ClassName(namespace, "ServiceBusProcessResult")

    val HandlerVersioningStrategy = ClassName(namespace, "HandlerVersioningStrategy")

    val Command = ClassName("$namespace.cqrs", "Command")
    val CommandHandler = ClassName("$namespace.cqrs", "CommandHandler")
    val CommandBus = ClassName("$namespace.cqrs", "CommandBus")

    val Query = ClassName("$namespace.cqrs", "Query")
    val QueryResult = ClassName("$namespace.cqrs", "QueryResult")
    val QueryHandler = ClassName("$namespace.cqrs", "QueryHandler")
    val QueryBus = ClassName("$namespace.cqrs", "QueryBus")

    val RequestHandlerNotFoundException = ClassName("$namespace.exception", "RequestHandlerNotFoundException")
    val QueryHandlerNotFoundException = ClassName("$namespace.exception", "QueryHandlerNotFoundException")

    val Message = ClassName(namespace, "Message")
    val MessageChannelDictionary = ClassName(namespace, "MessageChannelDictionary")

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

    val ComponentTypeDictionary = mapOf<String, ComponentType>(
        Aggregate.canonicalName to ComponentType.Aggregate,
        Error.canonicalName to ComponentType.Error,
        State.canonicalName to ComponentType.State,
        ReceivedData.canonicalName to ComponentType.ReceivedData,
        Event.canonicalName to ComponentType.Event,
        Command.canonicalName to ComponentType.Command,
        Query.canonicalName to ComponentType.Query,
        QueryResult.canonicalName to ComponentType.QueryResult,
        Request.canonicalName to ComponentType.Request,
        Response.canonicalName to ComponentType.Response
    )
}