package com.test.aggregate.kotlin

import net.ntworld.foundation.Implementation

@Implementation
class TodoOneImpl(override val id: String, isGenerated: Boolean) : TodoOne {
    override val state: TodoState = TodoState(id, isGenerated)

    override fun doSomething() {

    }
}