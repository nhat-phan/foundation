package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.MessagingSetting
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class MessageChannelDictionaryMainGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate Empty`() {
        val messagings = listOf<MessagingSetting>()
        val contracts = listOf(
            ClassInfo(
                packageName = "com.generator",
                className = "GetDataQuery"
            ),
            ClassInfo(
                packageName = "com.generator",
                className = "DataCreatedEvent"
            )
        )
        val generator = MessageChannelDictionaryMainGenerator()
        contracts.forEach { generator.add(it) }

        val result = generator.generate(messagings)
        assertGeneratedFileMatched(result, "MessageChannelDictionary/Empty.txt")
    }

    @Test
    fun `testGenerate NotEmpty`() {
        val messagings = listOf(
            MessagingSetting(
                contract = ClassInfo(
                    packageName = "com.generator",
                    className = "CreateSomethingRequest"
                ),
                channel = "service"
            ),
            MessagingSetting(
                contract = ClassInfo(
                    packageName = "com.generator",
                    className = "UpdateSomethingRequest"
                ),
                channel = "service"
            )
        )
        val contracts = listOf(
            ClassInfo(
                packageName = "com.generator",
                className = "GetDataQuery"
            ),
            ClassInfo(
                packageName = "com.generator",
                className = "DataCreatedEvent"
            )
        )
        val generator = MessageChannelDictionaryMainGenerator()
        contracts.forEach { generator.add(it) }

        val result = generator.generate(messagings)
        assertGeneratedFileMatched(result, "MessageChannelDictionary/NotEmpty.txt")
    }
}