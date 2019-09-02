package net.ntworld.foundation.processor

import net.ntworld.foundation.generator.setting.AggregateFactorySetting
import net.ntworld.foundation.generator.setting.EventSourcingSetting
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

    fun findAggregateFactorySettings(name: String): AggregateFactorySetting? {
        val settings = ProcessorOutput.readSettingsFileTest()
        return settings.aggregateFactories.firstOrNull {
            it.name == name
        }
    }

    fun findEventSettings(name: String): EventSourcingSetting? {
        val settings = ProcessorOutput.readSettingsFileTest()
        return settings.eventSourcings.firstOrNull {
            it.name == name
        }
    }
}