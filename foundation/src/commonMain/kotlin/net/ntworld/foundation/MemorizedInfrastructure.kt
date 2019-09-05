package net.ntworld.foundation

import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryBus
import net.ntworld.foundation.cqrs.ReceivedData
import net.ntworld.foundation.cqrs.QueryResult
import net.ntworld.foundation.eventSourcing.*
import kotlin.reflect.KClass

class MemorizedInfrastructure(base: Infrastructure) : InfrastructureWrapper(base) {
    private var _environment: Environment? = null
    private val _aggregateFactories = mutableMapOf<KClass<*>, AggregateFactory<*, *>>()
    private val _dataReceivers = mutableMapOf<KClass<*>, DataReceiver<*>>()
    private val _stores = mutableMapOf<KClass<*>, StateStore<*>>()
    private val _idGenerators = mutableMapOf<KClass<*>, IdGenerator>()
    private var _queryBus: QueryBus? = null
    private var _commandBus: CommandBus? = null
    private var _eventBus: EventBus? = null
    private var _serviceBus: ServiceBus? = null
    private var _encryptor: Encryptor? = null
    private var _faker: Faker? = null
    private var _encryptors = mutableMapOf<String, Encryptor>()
    private var _eventStreams = mutableMapOf<String, EventStream>()
    private var _eventConverterClasses = mutableMapOf<KClass<*>, EventConverter<*>>()
    private var _eventConverters = mutableMapOf<String, EventConverter<*>>()
    private var _messageTranslators = mutableMapOf<KClass<*>, MessageTranslator<*>>()
    private var _snapshotStores = mutableMapOf<KClass<*>, SnapshotStore<*>>()


    override fun environment(): Environment {
        if (null === _environment) {
            _environment = super.environment()
        }
        return _environment!!
    }

    @Suppress("UNCHECKED_CAST")
    override fun <A : Aggregate<S>, S : State> factoryOf(type: KClass<A>): AggregateFactory<A, S> {
        if (!_aggregateFactories.containsKey(type)) {
            _aggregateFactories[type] = super.factoryOf(type)
        }
        return _aggregateFactories[type] as AggregateFactory<A, S>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ReceivedData<Q, R>, Q : Query<R>, R: QueryResult> receiverOf(type: KClass<T>): DataReceiver<T> {
        if (!_dataReceivers.containsKey(type)) {
            _dataReceivers[type] = super.receiverOf(type)
        }
        return _dataReceivers[type] as DataReceiver<T>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <A : Aggregate<D>, D : State> storeOf(type: KClass<A>): StateStore<D> {
        if (!_stores.containsKey(type)) {
            _stores[type] = super.storeOf(type)
        }
        return _stores[type] as StateStore<D>
    }

    override fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator {
        if (!_idGenerators.containsKey(type)) {
            _idGenerators[type] = super.idGeneratorOf(type)
        }
        return _idGenerators[type] as IdGenerator
    }

    override fun queryBus(): QueryBus {
        if (null === _queryBus) {
            _queryBus = super.queryBus()
        }
        return _queryBus!!
    }

    override fun commandBus(): CommandBus {
        if (null === _commandBus) {
            _commandBus = super.commandBus()
        }
        return _commandBus!!
    }

    override fun eventBus(): EventBus {
        if (null === _eventBus) {
            _eventBus = super.eventBus()
        }
        return _eventBus!!
    }

    override fun serviceBus(): ServiceBus {
        if (null === _serviceBus) {
            _serviceBus = super.serviceBus()
        }
        return _serviceBus!!
    }

    override fun encryptor(): Encryptor {
        if (null === _encryptor) {
            _encryptor = super.encryptor()
        }
        return _encryptor!!
    }

    override fun encryptor(cipherId: String, algorithm: String): Encryptor {
        val key = "$cipherId:$algorithm"
        if (!_encryptors.containsKey(key)) {
            _encryptors[key] = super.encryptor(cipherId, algorithm)
        }
        return _encryptors[key] as Encryptor
    }

    override fun faker(): Faker {
        if (null === _faker) {
            _faker = super.faker()
        }
        return _faker!!
    }

    override fun eventStreamOf(eventSourced: AbstractEventSourced<*>, version: Int): EventStream {
        val key = "${eventSourced.streamType}:${eventSourced.id}:$version"
        if (!_eventStreams.containsKey(key)) {
            _eventStreams[key] = super.eventStreamOf(eventSourced, version)
        }
        return _eventStreams[key] as EventStream
    }

    @Suppress("UNCHECKED_CAST")
    override fun eventConverterOf(event: Event): EventConverter<Event> {
        val key = event::class
        if (!_eventConverterClasses.containsKey(key)) {
            _eventConverterClasses[key] = super.eventConverterOf(event)
        }
        return _eventConverterClasses[key] as EventConverter<Event>
    }

    @Suppress("UNCHECKED_CAST")
    override fun eventConverterOf(type: String, variant: Int): EventConverter<Event> {
        val key = "$type:$variant"
        if (!_eventConverters.containsKey(key)) {
            _eventConverters[key] = super.eventConverterOf(type, variant)
        }
        return _eventConverters[key] as EventConverter<Event>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> messageTranslatorOf(type: KClass<T>): MessageTranslator<T> {
        if (!_messageTranslators.containsKey(type)) {
            _messageTranslators[type] = super.messageTranslatorOf(type)
        }
        return _messageTranslators[type] as MessageTranslator<T>
    }

    @Suppress("UNCHECKED_CAST")
    override fun <A : Aggregate<S>, S : State> snapshotStoreOf(type: KClass<A>): SnapshotStore<S> {
        if (!_snapshotStores.containsKey(type)) {
            _snapshotStores[type] = super.snapshotStoreOf(type)
        }
        return _snapshotStores[type] as SnapshotStore<S>
    }
}