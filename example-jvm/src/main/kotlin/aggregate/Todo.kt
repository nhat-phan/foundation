package com.example.aggregate

import net.ntworld.foundation.Aggregate
import net.ntworld.foundation.Implementation
import net.ntworld.foundation.State
import net.ntworld.foundation.eventSourcing.AbstractEventSourced
import net.ntworld.foundation.eventSourcing.Event
import net.ntworld.foundation.eventSourcing.EventSourced

interface TodoState : State {
}

interface TodoA : Aggregate<TodoState> {
    fun create(task: String)
}

interface TodoB : Aggregate<TodoState> {
    fun create(task: String)
}

interface TodoC : Aggregate<TodoState> {
    fun create(task: String)
}

interface X : TodoC {

}

@Implementation
class TodoAImpl(override val id: String, override val state: TodoState) : TodoA {
    override fun create(task: String) {
    }
}

@EventSourced
class TodoBImplEsWrapperInvalid(base: TodoB) : TodoB by base {
    override fun create(task: String) {
    }
}

abstract class Y : AbstractEventSourced<TodoState>() {

}

@EventSourced
class TodoBImplEsWrapper(base: TodoB) : Y(), TodoB by base {
    override val streamType: String = ""

    override fun apply(event: Event) {
    }

    override fun create(task: String) {
    }
}

@Implementation
@EventSourced
class TodoCImplEsInvalid(
    override val id: String,
    override val state: TodoState
) : TodoC {
    override fun create(task: String) {
    }
}

@Implementation
@EventSourced
class TodoCImplEs(
    override val id: String,
    override val state: TodoState
) : AbstractEventSourced<TodoState>(), X {
    override val streamType: String = ""

    override fun apply(event: Event) {
    }

    override fun create(task: String) {
    }
}