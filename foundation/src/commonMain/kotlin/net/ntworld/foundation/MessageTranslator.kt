package net.ntworld.foundation

interface MessageTranslator<T> {
    fun toMessage(input: T): Message

    fun canConvert(message: Message): Boolean

    fun fromMessage(message: Message): T
}
