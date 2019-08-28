package net.ntworld.foundation

interface MessageChannelDictionary {
    fun lookupChannel(message: Message): String

    fun lookupReplyChannel(message: Message): String
}