package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.QueryHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class LocaQueryBusMainGeneratorTest: TestSuite() {
    @Test
    fun testGenerate() {
        val settings = listOf(
            QueryHandlerSetting(
                query = ClassInfo(className = "CreateQuery", packageName = "com.example.contract"),
                handler = ClassInfo(className = "CreateQueryHandler", packageName = "com.example.queryHandler"),
                makeByFactory = false,
                version = 0
            ),
            QueryHandlerSetting(
                query = ClassInfo(className = "UpdateQuery", packageName = "com.example.contract"),
                handler = ClassInfo(className = "UpdateQueryHandler", packageName = "com.example.queryHandler"),
                makeByFactory = true,
                version = 0
            ),
            QueryHandlerSetting(
                query = ClassInfo(className = "DeleteQuery", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeleteQueryHandler", packageName = "com.example.queryHandler"),
                makeByFactory = true,
                version = 0
            ),
            QueryHandlerSetting(
                query = ClassInfo(className = "DeleteQuery", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeleteQueryHandler", packageName = "com.example.queryHandler.v1"),
                makeByFactory = true,
                version = 1
            )
        )

        val file = LocalQueryBusMainGenerator().generate(settings)
        println(file.content)
    }
}