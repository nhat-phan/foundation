package net.ntworld.foundation.generator.util

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.type.Constructor
import net.ntworld.foundation.generator.type.Parameter
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstructorComposerTest : TestSuite() {
    @Test
    fun `test unique item parameters`() {
        val type = String::class.asTypeName()
        val dataset = mapOf(
            "First" to Constructor(
                parameters = listOf(
                    Parameter(name = "userId", type = type)
                )
            ),
            "Second" to Constructor(
                parameters = listOf(
                    Parameter(name = "companyId", type = type)
                )
            )
        )
        val composer = ConstructorComposer()
        dataset.forEach { composer.add(it.key, it.value) }

        assertMapEquals(
            mapOf(
                "userId" to type,
                "companyId" to type
            ),
            composer.composedParameters
        )
        assertMapEquals(
            mapOf(
                "First" to mapOf(
                    "userId" to "userId"
                ),
                "Second" to mapOf(
                    "companyId" to "companyId"
                )
            ),
            composer.items
        )
        assertEquals(
            Constructor(parameters = listOf(
                Parameter("userId", type),
                Parameter("companyId", type)
            )),
            composer.getComposedConstructor()
        )
    }

    @Test
    fun `test empty item parameters`() {
        val type = String::class.asTypeName()
        val dataset = mapOf(
            "First" to Constructor(
                parameters = listOf(
                    Parameter(name = "userId", type = type)
                )
            ),
            "Second" to Constructor(
                parameters = listOf()
            )
        )
        val composer = ConstructorComposer()
        dataset.forEach { composer.add(it.key, it.value) }

        assertMapEquals(
            mapOf(
                "userId" to type
            ),
            composer.composedParameters
        )
        assertMapEquals(
            mapOf(
                "First" to mapOf(
                    "userId" to "userId"
                ),
                "Second" to mapOf()
            ),
            composer.items
        )
        assertEquals(
            Constructor(parameters = listOf(
                Parameter("userId", type)
            )),
            composer.getComposedConstructor()
        )
    }

    @Test
    fun `test same item parameters`() {
        val type = String::class.asTypeName()
        val dataset = mapOf(
            "First" to Constructor(
                parameters = listOf(
                    Parameter(name = "userId", type = type),
                    Parameter(name = "companyId", type = type)
                )
            ),
            "Second" to Constructor(
                parameters = listOf(
                    Parameter(name = "companyId", type = type)
                )
            )
        )
        val composer = ConstructorComposer()
        dataset.forEach { composer.add(it.key, it.value) }

        assertMapEquals(
            mapOf(
                "userId" to type,
                "companyId" to type
            ),
            composer.composedParameters
        )
        assertMapEquals(
            mapOf(
                "First" to mapOf(
                    "userId" to "userId",
                    "companyId" to "companyId"
                ),
                "Second" to mapOf(
                    "companyId" to "companyId"
                )
            ),
            composer.items
        )
        assertEquals(
            Constructor(parameters = listOf(
                Parameter("userId", type),
                Parameter("companyId", type)
            )),
            composer.getComposedConstructor()
        )
    }

    @Test
    fun `test same item parameter name but different type`() {
        val type = String::class.asTypeName()
        val diffType = Int::class.asTypeName()
        val dataset = mapOf(
            "First" to Constructor(
                parameters = listOf(
                    Parameter(name = "userId", type = type),
                    Parameter(name = "companyId", type = diffType)
                )
            ),
            "Second" to Constructor(
                parameters = listOf(
                    Parameter(name = "companyId", type = type)
                )
            )
        )
        val composer = ConstructorComposer()
        dataset.forEach { composer.add(it.key, it.value) }

        assertMapEquals(
            mapOf(
                "userId" to type,
                "companyId" to diffType,
                "companyIdForSecond" to type
            ),
            composer.composedParameters
        )
        assertMapEquals(
            mapOf(
                "First" to mapOf(
                    "userId" to "userId",
                    "companyId" to "companyId"
                ),
                "Second" to mapOf(
                    "companyId" to "companyIdForSecond"
                )
            ),
            composer.items
        )
        assertEquals(
            Constructor(parameters = listOf(
                Parameter("userId", type),
                Parameter("companyId", diffType),
                Parameter("companyIdForSecond", type)
            )),
            composer.getComposedConstructor()
        )
    }

    @Test
    fun `test reserve type if it's not a kotlin type even they has different name`() {
        val kotlinType = String::class.asTypeName()
        val type = Framework.InfrastructureProvider
        val diffType = Framework.AggregateFactory
        val dataset = mapOf(
            "First" to Constructor(
                parameters = listOf(
                    Parameter(name = "userId", type = kotlinType),
                    Parameter(name = "infrastructure", type = type)
                )
            ),
            "Second" to Constructor(
                parameters = listOf(
                    Parameter(name = "infrastructureProvider", type = type),
                    Parameter(name = "factory", type = diffType)
                )
            )
        )
        val composer = ConstructorComposer()
        dataset.forEach { composer.add(it.key, it.value) }

        assertMapEquals(
            mapOf(
                "userId" to kotlinType,
                "infrastructure" to type,
                "factory" to diffType
            ),
            composer.composedParameters
        )
        assertMapEquals(
            mapOf(
                "First" to mapOf(
                    "userId" to "userId",
                    "infrastructure" to "infrastructure"
                ),
                "Second" to mapOf(
                    "infrastructureProvider" to "infrastructure",
                    "factory" to "factory"
                )
            ),
            composer.items
        )
        assertEquals(
            Constructor(parameters = listOf(
                Parameter("userId", kotlinType),
                Parameter("infrastructure", type),
                Parameter("factory", diffType)
            )),
            composer.getComposedConstructor()
        )
    }

    @Test
    fun `test generateComposedConstructor`() {
        val type = String::class.asTypeName()
        val diffType = Int::class.asTypeName()
        val dataset = mapOf(
            "First" to Constructor(
                parameters = listOf(
                    Parameter(name = "userId", type = type),
                    Parameter(name = "companyId", type = diffType)
                )
            ),
            "Second" to Constructor(
                parameters = listOf(
                    Parameter(name = "companyId", type = type)
                )
            )
        )
        val composer = ConstructorComposer()
        dataset.forEach { composer.add(it.key, it.value) }

        val fakedFile = getFakedFile()
        val fakedClass = TypeSpec.classBuilder("Test")
        composer.generateComposedConstructor(fakedClass)

        assertGeneratedContentMatched(
            fakedFile.addType(fakedClass.build()), """import kotlin.Int
import kotlin.String

class Test(
  private val userId: String,
  private val companyId: Int,
  private val companyIdForSecond: String
)
"""
        )
    }

    @Test
    fun `test generateNewInstanceCodeBlockFor`() {
        val type = String::class.asTypeName()
        val diffType = Int::class.asTypeName()
        val dataset = mapOf(
            "First" to Constructor(
                parameters = listOf(
                    Parameter(name = "userId", type = type),
                    Parameter(name = "companyId", type = diffType)
                )
            ),
            "Second" to Constructor(
                parameters = listOf(
                    Parameter(name = "companyId", type = type)
                )
            ),
            "Third" to Constructor(
                parameters = listOf()
            )
        )
        val composer = ConstructorComposer()
        dataset.forEach { composer.add(it.key, it.value) }

        val fakedFile = getFakedFile()
        val fakedClass = TypeSpec.classBuilder("Test")
        val fakedFun = FunSpec.builder("test")

        fakedFun
            .addCode("First")
            .addCode(composer.generateNewInstanceCodeBlockFor("First"))
            .addCode("\n")
            .addCode("Second")
            .addCode(composer.generateNewInstanceCodeBlockFor("Second"))
            .addCode("\n")
            .addCode("Third")
            .addCode(composer.generateNewInstanceCodeBlockFor("Third"))
            .addCode("\n")
            .addCode("NotFound")
            .addCode(composer.generateNewInstanceCodeBlockFor("NotFound"))
            .addCode("\n")

        fakedClass.addFunction(fakedFun.build())
        assertGeneratedContentMatched(
            fakedFile.addType(fakedClass.build()), """class Test {
  fun test() {
    First(
      userId = this.userId,
      companyId = this.companyId
    )
    Second(
      companyId = this.companyIdForSecond
    )
    Third()
    NotFound()
  }
}
"""
        )
    }

    private fun getFakedFile(): FileSpec.Builder {
        return FileSpec.builder("", "Faked")
    }

    private fun assertGeneratedContentMatched(file: FileSpec.Builder, content: String) {
        val stringBuffer = StringBuffer()
        file.build().writeTo(stringBuffer)
        assertEquals(
            content,
            stringBuffer.toString()
        )
    }
}