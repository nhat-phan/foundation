package net.ntworld.foundation

import kotlin.test.Test

class UseTest {

    interface SampleContract: Contract
    interface AnotherContract: Contract

    @Test
    fun `test syntax that Use annotation is repeatable`() {
        @Use(contract = SampleContract::class)
        @Use(contract = SampleContract::class)
        fun dummy1() {}

        @Use(contract = SampleContract::class)
        @Use(contract = AnotherContract::class)
        fun dummy2() {}

        @Use(contract = SampleContract::class)
        @Use(contract = SampleContract::class)
        class A

        @Use(contract = SampleContract::class)
        @Use(contract = AnotherContract::class)
        class B
    }
}