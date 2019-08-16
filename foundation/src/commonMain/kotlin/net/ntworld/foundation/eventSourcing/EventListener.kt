package net.ntworld.foundation.eventSourcing

interface EventListener {
    fun handleEvent(event: Event)
}