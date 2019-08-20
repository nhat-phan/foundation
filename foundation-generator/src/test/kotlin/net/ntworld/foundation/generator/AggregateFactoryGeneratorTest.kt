package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.AggregateFactorySettings
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class AggregateFactoryGeneratorTest {
    @Test
    fun testAbstractFactory() {
        val settings = AggregateFactorySettings(
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
        val file = AggregateFactoryGenerator.generate(settings)
        println(file.content)
    }

    @Test
    fun testWrapperFactoryWithEventSourced() {
        val settings = AggregateFactorySettings(
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
        val file = AggregateFactoryGenerator.generate(settings)
        println(file.content)
    }

    @Test
    fun testAbstractFactoryWithEventSourced() {
        val settings = AggregateFactorySettings(
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
        val file = AggregateFactoryGenerator.generate(settings)
        println(file.content)
    }
}