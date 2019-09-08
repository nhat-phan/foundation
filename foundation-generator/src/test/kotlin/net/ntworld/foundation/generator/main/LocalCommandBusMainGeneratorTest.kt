package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.CommandHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalCommandBusMainGeneratorTest : TestSuite() {
    @Test
    fun testGenerate() {
        val settings = listOf(
            CommandHandlerSetting(
                command = ClassInfo(className = "CreateCommand", packageName = "com.example.contract"),
                handler = ClassInfo(className = "CreateCommandHandler", packageName = "com.example.commandHandler"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = false,
                version = 0
            ),
            CommandHandlerSetting(
                command = ClassInfo(className = "UpdateCommand", packageName = "com.example.contract"),
                handler = ClassInfo(className = "UpdateCommandHandler", packageName = "com.example.commandHandler"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = true,
                version = 0
            ),
            CommandHandlerSetting(
                command = ClassInfo(className = "DeleteCommand", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeleteCommandHandler", packageName = "com.example.commandHandler"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = true,
                version = 0
            ),
            CommandHandlerSetting(
                command = ClassInfo(className = "DeleteCommand", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeleteCommandHandler", packageName = "com.example.commandHandler.v1"),
                metadata = KotlinMetadata.empty(),
                makeByFactory = true,
                version = 1
            )
        )

        val file = LocalCommandBusMainGenerator().generate(settings)
        // TODO: Rewrite generator and test
        // println(file.content)
    }
}