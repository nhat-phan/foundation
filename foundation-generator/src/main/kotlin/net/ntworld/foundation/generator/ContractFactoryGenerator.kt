package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.reflect.KClass

class ContractFactoryGenerator {
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
        val target = Utility.findContractFactoryTarget(items.map { it.value.factory }, namespace)
        val file = buildFile(target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    private fun buildFile(target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(target))

        return file.build()
    }

    private fun buildClass(target: ClassInfo): TypeSpec {
        val type = TypeSpec.objectBuilder(target.className)

        items.values.forEach {
            val of = FunSpec.builder("of")
            of.addParameter(
                "contract",
                KClass::class.asTypeName().parameterizedBy(it.contract.toClassName())
            )
            of.returns(it.contract.toClassName())
            of.addCode("return %T\n", it.factory.toClassName())

            type.addFunction(of.build())
        }

        return type.build()
    }
}