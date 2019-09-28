package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorOutput
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.type.ClassInfo

class MessageTranslatorResolverMainGenerator {
    private val data = mutableMapOf<String, ClassInfo>()

    fun add(contract: ClassInfo) {
        data[contract.fullName()] = contract
    }

    fun generate(packageName: String): GeneratedFile {
        val target = Utility.findMessageTranslatorResolverTarget(packageName)
        val file = buildFile(target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return GeneratedFile.makeMainFile(target, file.toString())
    }

    private fun buildFile(target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(target))

        return file.build()
    }

    private fun buildClass(target: ClassInfo): TypeSpec {
        val type = TypeSpec
            .objectBuilder(target.toClassName())

        type.addFunction(buildResolveFunction())

        return type.build()
    }

    private fun buildGuessKClassFunction(): FunSpec {
        val code = CodeBlock.builder()
        code.add("return infrastructure.root.messageTranslatorOf(guessKClass(instance))")

        return FunSpec.builder("resolve")
            .addParameter("infrastructure", Framework.Infrastructure)
            .addParameter("instance", Any::class)
            .returns(Framework.MessageTranslator)
            .addCode(code.build())
            .build()
    }

    private fun buildResolveFunction(): FunSpec {
        val code = CodeBlock.builder()
        code.add("return infrastructure.root.messageTranslatorOf(guessKClass(instance))")

        return FunSpec.builder("resolve")
            .addParameter("infrastructure", Framework.Infrastructure)
            .addParameter("instance", Any::class)
            .returns(Framework.MessageTranslator)
            .addCode(code.build())
            .build()
    }
}