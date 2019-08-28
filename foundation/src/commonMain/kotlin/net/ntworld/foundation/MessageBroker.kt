package net.ntworld.foundation

import kotlinx.coroutines.channels.Channel

interface MessageBroker {
    fun send(message: Message)

    fun send(message: Message, replyTo: Channel<Message>, timeout: Int)
}