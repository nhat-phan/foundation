package com.example.handler

import com.example.TestSuite
import com.example.contract.OneSupertypeContract
import com.example.make
import kotlin.test.Test

class YourTest : TestSuite() {
    @Test
    fun testSomething() {
        println(OneSupertypeContract.make())
    }
}