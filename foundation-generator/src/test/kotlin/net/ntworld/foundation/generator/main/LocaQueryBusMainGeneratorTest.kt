package net.ntworld.foundation.generator.main

import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.QueryHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import kotlin.test.Test
import kotlin.test.assertEquals

class LocaQueryBusMainGeneratorTest: TestSuite() {
    @Test
    fun testGenerate() {
        val settings = listOf(
            QueryHandlerSetting(
                query = ClassInfo(className = "CreateQuery", packageName = "com.example.contract"),
                handler = ClassInfo(className = "CreateQueryHandler", packageName = "com.example.queryHandler"),
                makeByFactory = false,
                metadata = KotlinMetadata.empty(),
                version = 0
            ),
            QueryHandlerSetting(
                query = ClassInfo(className = "UpdateQuery", packageName = "com.example.contract"),
                handler = ClassInfo(className = "UpdateQueryHandler", packageName = "com.example.queryHandler"),
                makeByFactory = true,
                metadata = KotlinMetadata.empty(),
                version = 0
            ),
            QueryHandlerSetting(
                query = ClassInfo(className = "DeleteQuery", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeleteQueryHandler", packageName = "com.example.queryHandler"),
                makeByFactory = true,
                metadata = KotlinMetadata.empty(),
                version = 0
            ),
            QueryHandlerSetting(
                query = ClassInfo(className = "DeleteQuery", packageName = "com.example.contract"),
                handler = ClassInfo(className = "DeleteQueryHandler", packageName = "com.example.queryHandler.v1"),
                makeByFactory = true,
                metadata = KotlinMetadata.empty(),
                version = 1
            )
        )

        val file = LocalQueryBusMainGenerator().generate(settings)
        // TODO: Rewrite generator and test
        // println(file.content)
        assertEquals(pinnedContent, file.content)
    }

    private val pinnedContent = """package com.example.queryHandler

import com.example.contract.CreateQuery
import com.example.contract.DeleteQuery
import com.example.contract.UpdateQuery
import kotlin.Suppress
import net.ntworld.foundation.HandlerVersioningStrategy
import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.LocalBusResolver
import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryBus
import net.ntworld.foundation.cqrs.QueryHandler
import net.ntworld.foundation.exception.QueryHandlerNotFoundException

abstract class LocalQueryBus(
  val infrastructure: Infrastructure
) : QueryBus, LocalBusResolver<Query<*>, QueryHandler<*, *>> {
  @Suppress("UNCHECKED_CAST")
  override fun <R: net.ntworld.foundation.cqrs.QueryResult> process(query: Query<R>): R {
    val handler = this.resolve(query)
    if (null !== handler) {
      return handler.execute(query = query, message = null) as R
    }
    throw QueryHandlerNotFoundException(query.toString())
  }

  open fun getVersioningStrategy(query: Query<*>): HandlerVersioningStrategy =
      HandlerVersioningStrategy.useLatestVersion

  protected abstract fun makeUpdateQueryHandler(): UpdateQueryHandler

  protected abstract fun makeDeleteQueryHandler(): com.example.queryHandler.v1.DeleteQueryHandler

  protected abstract fun make_com_example_queryHandler_DeleteQueryHandler(): DeleteQueryHandler

  override fun resolve(instance: Query<*>): QueryHandler<*, *>? {
    val strategy = getVersioningStrategy(instance)
    if (strategy.skip()) {
      return null
    }

    return when (instance) {
      is CreateQuery -> CreateQueryHandler(infrastructure)

      is UpdateQuery -> makeUpdateQueryHandler()

      is DeleteQuery -> {
        if (strategy.useLatestVersion()) {
          return makeDeleteQueryHandler()
        }

        return when (strategy.specificVersion) {
          0 -> make_com_example_queryHandler_DeleteQueryHandler()
          1 -> makeDeleteQueryHandler()
          else -> null
        }
      }

      else -> null
    }
  }
}
"""
}