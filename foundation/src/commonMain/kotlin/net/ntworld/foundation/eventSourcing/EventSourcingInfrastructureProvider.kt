package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.*
import kotlin.reflect.KClass

open class EventSourcingInfrastructureProvider(
    private var next: EventSourcingInfrastructure? = null
) : InfrastructureProvider(next), EventSourcingInfrastructure {


//    fun setNext(next: EventSourcingInfrastructure): Infrastructure {
//        this.next = next
//
//        return super.setNext(next)
//    }

    override fun eventBus(): EventBus {
        if (null !== next) {
            return next!!.eventBus()
        }
        throw CannotResolveException("EventSourcingInfrastructure.eventBus() cannot be resolved")
    }

    override fun encryptor(): Encryptor {
        if (null !== next) {
            return next!!.encryptor()
        }
        throw CannotResolveException("EventSourcingInfrastructure.encryptor() cannot be resolved")
    }

    override fun encryptor(cipherId: String, algorithm: String): Encryptor {
        if (null !== next) {
            return next!!.encryptor(cipherId, algorithm)
        }
        throw CannotResolveException("EventSourcingInfrastructure.encryptor() cannot resolve ($cipherId, $algorithm)")
    }

    override fun eventStreamOf(eventSourced: AbstractEventSourced, version: Int): EventStream {
        if (null !== next) {
            return next!!.eventStreamOf(eventSourced, version)
        }
        throw CannotResolveException("EventSourcingInfrastructure.eventStreamOf() cannot resolve ($eventSourced, $version)")
    }

    override fun eventConverter(eventType: String): EventConverter<Event> {
        if (null !== next) {
            return next!!.eventConverter(eventType)
        }
        throw CannotResolveException("EventSourcingInfrastructure.eventConverter() cannot resolve $eventType")
    }

    override fun <T : Aggregate> snapshotStoreOf(type: KClass<T>): SnapshotStore<T> {
        if (null !== next) {
            return next!!.snapshotStoreOf(type)
        }
        throw CannotResolveException("EventSourcingInfrastructure.snapshotStoreOf() cannot resolve $type")
    }

}