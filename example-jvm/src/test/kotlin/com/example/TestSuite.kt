package com.example

import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.LocalBusResolver
import net.ntworld.foundation.MemorizedInfrastructure
import net.ntworld.foundation.MessageBroker
import net.ntworld.foundation.MessageChannelDictionary
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.CommandHandler
import net.ntworld.foundation.fluency.firstCall
import kotlin.test.Test

open class TestSuite {

}
//
//class RemoteCommandBus(
//    private val infrastructure: Infrastructure,
//    private val bus: LocalCommandBus,
//    private val messageBroker: MessageBroker,
//    private val messageChannelDictionary: MessageChannelDictionary
//) : CommandBus, LocalBusResolver<Command, CommandHandler<*>> {
//    override fun resolve(instance: Command): CommandHandler<*>? = bus.resolve(instance)
//
//    override fun process(command: Command) {
//        val handler = resolve(command)
//        if (null !== handler) {
//            bus.process(command)
//        } else {
//            val message = infrastructure.root.messageTranslatorOf(command::class)
//            val channel = messageChannelDictionary.lookupChannel(message)
//        }
//    }
//
//}

//class MockableInfrastructure(private val infrastructure: Infrastructure) :
//    Infrastructure by MemorizedInfrastructure(infrastructure) {
//
//    override fun commandBus(): CommandBus {
//        TODO()
//    }
//}

class TestSomething {
    @Test
    fun run() {
        val bus = MockableServiceBus(LocalServiceBus())

        bus shouldProcess TestMockRequest on firstCall match {
            it.name == "test"
        }

        bus.process(TestMockRequest.make(name = "test"))

        bus.verifyAll()
    }
}