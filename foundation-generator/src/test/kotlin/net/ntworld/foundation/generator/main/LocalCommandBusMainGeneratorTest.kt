package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.CommandHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.test.Test

class LocalCommandBusMainGeneratorTest: TestSuite() {
    @Test
    fun testGenerate() {
        val settings = listOf(
            CommandHandlerSetting(
                command = ClassInfo(className = "CreateCommand", packageName = "com.example.contract"),
                handler = ClassInfo(className = "CreateCommandHandler", packageName = "com.example.commandHandler"),
                makeByFactory = false,
                version = 0
            ),
            CommandHandlerSetting(
                command = ClassInfo(className = "UpdateCommand", packageName = "com.example.contract"),
                handler = ClassInfo(className = "UpdateCommandHandler", packageName = "com.example.commandHandler"),
                makeByFactory = true,
                version = 0
            ),
            CommandHandlerSetting(
                command = ClassInfo(className = "DeleteCommand", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeleteCommandHandler", packageName = "com.example.commandHandler"),
                makeByFactory = true,
                version = 0
            ),
            CommandHandlerSetting(
                command = ClassInfo(className = "DeleteCommand", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeleteCommandHandler", packageName = "com.example.commandHandler.v1"),
                makeByFactory = true,
                version = 1
            )
        )

        val file = LocalCommandBusMainGenerator().generate(settings)
        println(file.content)
    }
}