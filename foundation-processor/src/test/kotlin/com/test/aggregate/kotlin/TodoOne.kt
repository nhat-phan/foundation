package com.test.aggregate.kotlin

import net.ntworld.foundation.Aggregate

interface TodoOne: Aggregate<TodoState> {
    fun doSomething()
}