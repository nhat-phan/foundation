package net.ntworld.foundation.generator.test

import net.ntworld.foundation.generator.Platform
import net.ntworld.foundation.generator.TestSuite
import kotlin.test.Test
import kotlin.test.assertEquals

class UtilityTestGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate for Jvm`() {
        val utilityTestGenerator = UtilityTestGenerator(Platform.Jvm)
        val result = utilityTestGenerator.generate("com.generator")
        assertEquals("TestUtility", result.target.className)
        assertGeneratedFileMatched(result, "Utility/Jvm.txt")
    }
}