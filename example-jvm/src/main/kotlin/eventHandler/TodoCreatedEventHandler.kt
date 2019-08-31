package com.example.eventHandler

import com.example.event.TodoCreatedEvent
import net.ntworld.foundation.EventHandler
import net.ntworld.foundation.Handler
import net.ntworld.foundation.Infrastructure

@Handler
class TodoCreatedEventHandler(infrastructure: Infrastructure) : EventHandler<TodoCreatedEvent> {
    override fun handle(event: TodoCreatedEvent) {
    }
}