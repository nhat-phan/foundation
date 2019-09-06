package net.ntworld.foundation.processor

import net.ntworld.foundation.generator.setting.AggregateFactorySetting
import net.ntworld.foundation.generator.setting.EventSourcingSetting
import net.ntworld.foundation.processor.util.ProcessorOutput
import java.util.*
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
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


    private class ProcessingEnv: ProcessingEnvironment {
        override fun getElementUtils(): Elements { throw Exception() }
        override fun getTypeUtils(): Types { throw Exception() }
        override fun getMessager(): Messager { throw Exception() }
        override fun getLocale(): Locale { throw Exception() }
        override fun getSourceVersion(): SourceVersion { throw Exception() }
        override fun getOptions(): MutableMap<String, String> { throw Exception() }
        override fun getFiler(): Filer { throw Exception() }
    }

    fun findAggregateFactorySettings(name: String): AggregateFactorySetting? {
        val settings = ProcessorOutput.readSettingsFile(ProcessingEnv())
        return settings.aggregateFactories.firstOrNull {
            it.name == name
        }
    }

    fun findEventSettings(name: String): EventSourcingSetting? {
        val settings = ProcessorOutput.readSettingsFile(ProcessingEnv())
        return settings.eventSourcings.firstOrNull {
            it.name == name
        }
    }
}