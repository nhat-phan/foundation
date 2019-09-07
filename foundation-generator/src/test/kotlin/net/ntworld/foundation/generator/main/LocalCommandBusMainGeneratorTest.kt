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
        assertEquals(pinnedContent, file.content)
    }

    val pinnedContent = """package com.example.commandHandler

import com.example.contract.CreateCommand
import com.example.contract.DeleteCommand
import com.example.contract.UpdateCommand
import net.ntworld.foundation.HandlerVersioningStrategy
import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.LocalBusResolver
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.CommandBus
import net.ntworld.foundation.cqrs.CommandHandler

abstract class LocalCommandBus(
  val infrastructure: Infrastructure
) : CommandBus, LocalBusResolver<Command, CommandHandler<*>> {
  override fun process(command: Command) {
    val handler = this.resolve(command)
    if (null !== handler) {
      handler.execute(command = command, message = null)
    }
  }

  open fun getVersioningStrategy(command: Command): HandlerVersioningStrategy =
      HandlerVersioningStrategy.useLatestVersion

  protected abstract fun makeUpdateCommandHandler(): UpdateCommandHandler

  protected abstract fun makeDeleteCommandHandler():
      com.example.commandHandler.v1.DeleteCommandHandler

  protected abstract fun make_com_example_commandHandler_DeleteCommandHandler():
      DeleteCommandHandler

  override fun resolve(instance: Command): CommandHandler<*>? {
    val strategy = getVersioningStrategy(instance)
    if (strategy.skip()) {
      return null
    }

    return when (instance) {
      is CreateCommand -> CreateCommandHandler(infrastructure)

      is UpdateCommand -> makeUpdateCommandHandler()

      is DeleteCommand -> {
        if (strategy.useLatestVersion()) {
          return makeDeleteCommandHandler()
        }

        return when (strategy.specificVersion) {
          0 -> make_com_example_commandHandler_DeleteCommandHandler()
          1 -> makeDeleteCommandHandler()
          else -> null
        }
      }

      else -> null
    }
  }
}
"""
}