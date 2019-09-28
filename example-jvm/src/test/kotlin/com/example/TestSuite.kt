package com.example

import com.example.contract.CreateTodoCommand
import net.ntworld.foundation.*
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.CommandHandler
import net.ntworld.foundation.fluency.firstCall
import kotlin.reflect.KClass
import kotlin.test.Test

open class TestSuite {

}

fun Command.toMessage(infrastructure: Infrastructure): Message {
    return when(this) {
        is CreateTodoCommand -> infrastructure.messageTranslatorOf(CreateTodoCommand::class).toMessage(this)
        else -> {
            throw Exception()
        }
    }
}

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
//            val message = command.toMessage(infrastructure)
//            val channel = messageChannelDictionary.lookupChannel(message)
//            val replyChannel = messageChannelDictionary.lookupReplyChannel(message)
//            messageBroker.send(message, replyTo, 30000)
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