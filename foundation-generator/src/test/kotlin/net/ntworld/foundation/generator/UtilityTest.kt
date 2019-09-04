package net.ntworld.foundation.generator

import kotlin.test.Test
import kotlin.test.assertEquals

class UtilityTest : TestSuite() {
    @Test
    fun testGuessPackageName() {
        data class DataItem(val current: String, val given: String, val result: String)

        val dataset = listOf(
            DataItem(current = "", given = "", result = ""),
            DataItem(current = "com.company", given = "", result = "com.company"),
            DataItem(current = "", given = "com.company", result = "com.company"),
            DataItem(current = "com.company", given = "com.company.event", result = "com.company"),
            DataItem(current = "com.company.event", given = "com.company", result = "com.company"),
            DataItem(current = "com.company.event", given = "com.company.event", result = "com.company.event"),
            DataItem(current = "com.company.event", given = "com.company.factory", result = "com.company"),
            DataItem(current = "com.company.factory", given = "com.company.event", result = "com.company"),
            DataItem(current = "com.company.factory", given = "com.company.module.event", result = "com.company"),
            DataItem(current = "com.company.module.factory", given = "com.company.event", result = "com.company"),
            DataItem(
                current = "com.company.module.event",
                given = "com.company.module.factory",
                result = "com.company.module"
            ),
            DataItem(current = "com.company", given = "org.organization.factory", result = "com.company")
        )

        dataset.forEach {
            assertEquals(it.result, Utility.guessPackageName(it.current, it.given))
        }
    }
}