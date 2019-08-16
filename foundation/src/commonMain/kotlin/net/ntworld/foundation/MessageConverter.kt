package net.ntworld.foundation

interface MessageConverter<T> {
    fun toMessage(input: T): Message

    fun fromMessage(message: Message): T
}
