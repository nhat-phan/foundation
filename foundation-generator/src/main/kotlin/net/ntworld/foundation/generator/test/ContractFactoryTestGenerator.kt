package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.reflect.KClass

class ContractFactoryTestGenerator(private val platform: Platform) {
    data class Item(
        val contract: ClassInfo,
        val factory: ClassInfo
    )

    private val items = mutableMapOf<String, Item>()

    fun add(contract: ClassInfo, factory: ClassInfo) {
        items[contract.fullName()] = Item(
            contract = contract,
            factory = factory
        )
    }

    fun generate(namespace: String? = null): GeneratedFile {
        val target = Utility.findContractFactoryTarget(
            items.map { it.value.factory },
            namespace
        )
        val file = buildFile(target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildTestGeneratedFile(target, stringBuffer.toString())
    }

    private fun buildFile(target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(target))

        return file.build()
    }

    private fun buildClass(target: ClassInfo): TypeSpec {
        val type = TypeSpec.objectBuilder(target.className)

        val code = when (platform) {
            Platform.Jvm -> getInitFakerForJvm()
        }

        type.addProperty(
            PropertySpec.builder("faker", Framework.Faker)
                .addModifiers(KModifier.PRIVATE)
                .initializer(code)
                .build()
        )

        items.values.forEach {
            val of = FunSpec.builder("of")
            of.addParameter(
                "contract",
                KClass::class.asTypeName().parameterizedBy(it.contract.toClassName())
            )
            of.returns(it.contract.toClassName())
            of.addCode("return %T(faker)\n", it.factory.toClassName())

            type.addFunction(of.build())
        }

        return type.build()
    }

    private fun getInitFakerForJvm(): CodeBlock {
        val code = CodeBlock.builder()
        code.add("%T(%T())\n", Framework.JavaFakerWrapper, Framework.JavaFaker)

        return code.build()
    }
}