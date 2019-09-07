package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.RequestHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import kotlin.test.Test

class LocalServiceBusGeneratorTest : TestSuite() {
    @Test
    fun testGenerate() {
        val settings = listOf(
            RequestHandlerSetting(
                request = ClassInfo(className = "CreateRequest", packageName = "com.example.contract"),
                handler = ClassInfo(className = "CreateRequestHandler", packageName = "com.example.requestHandler"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = false,
                version = 0
            ),
            RequestHandlerSetting(
                request = ClassInfo(className = "UpdateRequest", packageName = "com.example.contract"),
                handler = ClassInfo(className = "UpdateRequestHandler", packageName = "com.example.requestHandler"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = true,
                version = 0
            ),
            RequestHandlerSetting(
                request = ClassInfo(className = "DeleteRequest", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeleteRequestHandler", packageName = "com.example.requestHandler"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = true,
                version = 0
            ),
            RequestHandlerSetting(
                request = ClassInfo(className = "DeleteRequest", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeleteRequestHandler", packageName = "com.example.requestHandler.v1"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = true,
                version = 1
            )
        )

        val file = LocalServiceBusMainGenerator().generate(settings)
        println(file.content)
    }
}