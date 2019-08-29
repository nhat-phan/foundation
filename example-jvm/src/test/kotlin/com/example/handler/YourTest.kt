package com.example.handler

import com.example.TestSuite
import kotlin.test.Test

class YourTest : TestSuite() {
    @Test
    fun testSomething() {
        println(contracts.makeCreateTodoCommand())
    }
}