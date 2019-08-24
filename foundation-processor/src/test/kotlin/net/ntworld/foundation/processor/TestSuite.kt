package net.ntworld.foundation.processor

import net.ntworld.foundation.ProcessorOutput
import net.ntworld.foundation.generator.setting.AggregateFactorySettings
import net.ntworld.foundation.generator.setting.EventSettings
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class TestSuite {
    @BeforeTest
    fun setUp() {
        ProcessorOutput.setupTest()
    }

    @AfterTest
    fun tearDown() {
        ProcessorOutput.tearDownTest()
    }

    fun findAggregateFactorySettings(name: String): AggregateFactorySettings? {
        val settings = ProcessorOutput.readSettingsFileTest()
        return settings.aggregateFactories.firstOrNull {
            it.name == name
        }
    }

    fun findEventSettings(name: String): EventSettings? {
        val settings = ProcessorOutput.readSettingsFileTest()
        return settings.events.firstOrNull {
            it.name == name
        }
    }
}