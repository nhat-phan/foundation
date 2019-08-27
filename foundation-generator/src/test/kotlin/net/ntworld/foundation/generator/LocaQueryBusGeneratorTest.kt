package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.QueryHandlerSettings
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class LocaQueryBusGeneratorTest {
    @BeforeTest
    fun setUp() {
        GeneratorOutput.setupTest()
    }

    @AfterTest
    fun tearDown() {
        GeneratorOutput.tearDownTest()
    }

    @Test
    fun testGenerate() {
        val settings = listOf(
            QueryHandlerSettings(
                query = ClassInfo(className = "CreateQuery", packageName = "com.example.contract"),
                bus = "",
                handler = ClassInfo(className = "CreateQueryHandler", packageName = "com.example.queryHandler"),
                makeByFactory = false,
                version = 0
            ),
            QueryHandlerSettings(
                query = ClassInfo(className = "UpdateQuery", packageName = "com.example.contract"),
                bus = "",
                handler = ClassInfo(className = "UpdateQueryHandler", packageName = "com.example.queryHandler"),
                makeByFactory = true,
                version = 0
            ),
            QueryHandlerSettings(
                query = ClassInfo(className = "DeleteQuery", packageName = "com.example.contract"),
                bus = "",
                handler = ClassInfo(className = "DeleteQueryHandler", packageName = "com.example.queryHandler"),
                makeByFactory = true,
                version = 0
            ),
            QueryHandlerSettings(
                query = ClassInfo(className = "DeleteQuery", packageName = "com.example.contract"),
                bus = "",
                handler = ClassInfo(className = "DeleteQueryHandler", packageName = "com.example.queryHandler.v1"),
                makeByFactory = true,
                version = 1
            )
        )

        val file = LocalQueryBusGenerator().generate(settings)
        println(file.content)
    }
}