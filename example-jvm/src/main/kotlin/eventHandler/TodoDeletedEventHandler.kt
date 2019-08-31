package com.example.eventHandler

import com.example.event.TodoDeletedEvent
import net.ntworld.foundation.EventHandler
import net.ntworld.foundation.Handler

@Handler
class TodoDeletedEventHandler(
) : EventHandler<TodoDeletedEvent> {
    override fun handle(event: TodoDeletedEvent) {
    }
}