package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.AggregateFactorySetting
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class AggregateFactoryMainGeneratorTest: TestSuite() {
    @Test
    fun testAbstractFactory() {
        val settings = AggregateFactorySetting(
            aggregate = ClassInfo(
                packageName = "test.aggregate",
                className = "Todo"
            ),
            state = ClassInfo(
                packageName = "test.state",
                className = "TodoState"
            ),
            implementation = ClassInfo(
                packageName = "test.aggregate",
                className = "TodoImpl"
            ),
            isAbstract = true,
            isEventSourced = false
        )
        val file = AggregateFactoryMainGenerator.generate(settings)
        println(file.content)
    }

    @Test
    fun testWrapperFactoryWithEventSourced() {
        val settings = AggregateFactorySetting(
            aggregate = ClassInfo(
                packageName = "test.aggregate",
                className = "Todo"
            ),
            state = ClassInfo(
                packageName = "test.state",
                className = "TodoState"
            ),
            implementation = ClassInfo(
                packageName = "test.aggregate",
                className = "TodoImpl"
            ),
            isAbstract = false,
            isEventSourced = true
        )
        val file = AggregateFactoryMainGenerator.generate(settings)
        println(file.content)
    }

    @Test
    fun testAbstractFactoryWithEventSourced() {
        val settings = AggregateFactorySetting(
            aggregate = ClassInfo(
                packageName = "test.aggregate",
                className = "Todo"
            ),
            state = ClassInfo(
                packageName = "test.state",
                className = "TodoState"
            ),
            implementation = ClassInfo(
                packageName = "test.aggregate",
                className = "TodoImpl"
            ),
            isAbstract = true,
            isEventSourced = true
        )
        val file = AggregateFactoryMainGenerator.generate(settings)
        println(file.content)
    }
}