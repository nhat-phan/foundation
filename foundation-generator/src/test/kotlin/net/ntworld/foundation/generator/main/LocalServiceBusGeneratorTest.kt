package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.RequestHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import kotlin.test.Test
import kotlin.test.assertEquals

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
        // TODO: Rewrite generator and test
        // println(file.content)
        assertEquals(pinnedContent, file.content)
    }

    private val pinnedContent = """package com.example.requestHandler

import com.example.contract.CreateRequest
import com.example.contract.DeleteRequest
import com.example.contract.UpdateRequest
import kotlin.Suppress
import net.ntworld.foundation.HandlerVersioningStrategy
import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.LocalBusResolver
import net.ntworld.foundation.Request
import net.ntworld.foundation.RequestHandler
import net.ntworld.foundation.ServiceBus
import net.ntworld.foundation.ServiceBusProcessResult
import net.ntworld.foundation.exception.RequestHandlerNotFoundException

abstract class LocalServiceBus(
  val infrastructure: Infrastructure
) : ServiceBus, LocalBusResolver<Request<*>, RequestHandler<*, *>> {
  @Suppress("UNCHECKED_CAST")
  override fun <R: net.ntworld.foundation.Response> process(request: Request<R>):
      ServiceBusProcessResult<R> {
    val handler = this.resolve(request)
    if (null !== handler) {
      return ServiceBusProcessResult.make(handler.execute(request = request, message = null) as R)
    }
    throw RequestHandlerNotFoundException(request.toString())
  }

  open fun getVersioningStrategy(request: Request<*>): HandlerVersioningStrategy =
      HandlerVersioningStrategy.useLatestVersion

  protected abstract fun makeUpdateRequestHandler(): UpdateRequestHandler

  protected abstract fun makeDeleteRequestHandler():
      com.example.requestHandler.v1.DeleteRequestHandler

  protected abstract fun make_com_example_requestHandler_DeleteRequestHandler():
      DeleteRequestHandler

  override fun resolve(instance: Request<*>): RequestHandler<*, *>? {
    val strategy = getVersioningStrategy(instance)
    if (strategy.skip()) {
      return null
    }

    return when (instance) {
      is CreateRequest -> CreateRequestHandler(infrastructure)

      is UpdateRequest -> makeUpdateRequestHandler()

      is DeleteRequest -> {
        if (strategy.useLatestVersion()) {
          return makeDeleteRequestHandler()
        }

        return when (strategy.specificVersion) {
          0 -> make_com_example_requestHandler_DeleteRequestHandler()
          1 -> makeDeleteRequestHandler()
          else -> null
        }
      }

      else -> null
    }
  }
}
"""
}