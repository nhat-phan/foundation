package net.ntworld.foundation

// because this class is so simple, so the whole class will be generated automatically by generator
abstract class AbstractLocalEventBus : EventBus, LocalBusResolver<Event, List<EventHandler<Event>>> {
    override fun publish(event: Event) {
        this.process(event)
    }

    override fun process(event: Event, message: Message?) {
        val handlers = this.resolve(event)
        if (null !== handlers) {
            handlers.forEach { it.handle(event = event, message = message) }
        }
    }
}

//class RemoteEventBus(private val bus: LocalBusResolver<Event, List<EventHandler<Event>>) : EventBus {
//    override fun publish(event: Event) {
//        this.process(event)
//
//        // publish event to remote buses
//    }
//
//    override fun process(event: Event, message: Message?) {
//        val handlers = bus.resolve(event)
//        if (null !== handlers) {
//            handlers.forEach { it.handle(event = event, message = message) }
//        }
//    }
//}
//
//abstract class AbstractLocalCommandBus: CommandBus, LocalBusResolver<Command, CommandHandler<Command>> {
//    override fun process(command: Command, message: Message?) {
//        val handler = this.resolve(command)
//        if (null !== handler) {
//            handler.handle(command = command, message = message)
//        }
//    }
//}
//
//class RemoteCommandBus(private val bus: LocalBusResolver<Command, CommandHandler<Command>>): CommandBus {
//    override fun process(command: Command, message: Message?) {
//        // do the same job if there is a handler in local
//        val handler = this.bus.resolve(command)
//        if (null !== handler) {
//            return handler.handle(command = command, message = message)
//        }
//
//        // call remote command bus if there is no local handler
//    }
//}
//
//abstract class AbstractLocalQueryBus: QueryBus, LocalBusResolver<Query<*>, QueryHandler<Query<*>, *>> {
//    override fun <R> process(query: Query<R>, message: Message?): R {
//        val handler = this.resolve(query)
//        if (null !== handler) {
//            return handler.handle(query = query, message = message) as R
//        }
//        throw Exception("Query not found")
//    }
//}
//
//class RemoteQueryBus(private val bus: LocalBusResolver<Query<*>, QueryHandler<Query<*>, *>>): QueryBus {
//    override fun <R> process(query: Query<R>, message: Message?): R {
//        // do the same job if there is a handler in local
//        val handler = this.bus.resolve(query)
//        if (null !== handler) {
//            return handler.handle(query = query, message = message) as R
//        }
//
//        // call remote command bus if there is no local handler
//        throw Exception("Query not found")
//    }
//}