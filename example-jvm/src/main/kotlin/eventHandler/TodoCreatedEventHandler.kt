package com.example.eventHandler

import com.example.event.TodoCreatedEvent
import net.ntworld.foundation.EventHandler
import net.ntworld.foundation.Handler

@Handler
class TodoCreatedEventHandler : EventHandler<TodoCreatedEvent> {
    override fun handle(event: TodoCreatedEvent) {
    }
}