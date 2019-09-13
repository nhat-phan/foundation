package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorOutput
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.MessagingSetting
import net.ntworld.foundation.generator.type.ClassInfo

class MessageChannelDictionaryMainGenerator {
    private val items = mutableMapOf<String, ClassInfo>()

    fun add(contract: ClassInfo) {
        items[contract.fullName()] = contract
    }

    fun generate(messagings: List<MessagingSetting>, namespace: String? = null): GeneratedFile {
        val target = Utility.findMessageChannelDictionaryTarget(
            messagings.map { it.contract },
            namespace
        )
        val file = buildFile(messagings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return GeneratedFile.makeMainFile(target, stringBuffer.toString())
    }

    private fun buildFile(messagings: List<MessagingSetting>, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(messagings, target))

        return file.build()
    }

    private fun buildClass(messagings: List<MessagingSetting>, target: ClassInfo): TypeSpec {
        val type = TypeSpec.classBuilder(target.className)
        type.addSuperinterface(Framework.MessageChannelDictionary)
        type.addModifiers(KModifier.OPEN)

        type.addFunction(buildGetDefaultChannelFun())
        type.addFunction(buildResolveChannelByBodyType())
        type.addFunction(buildLookupReplyChannel())
        type.addFunction(buildLookupChannel(messagings))

        return type.build()
    }

    private fun buildGetDefaultChannelFun(): FunSpec {
        return FunSpec.builder("getDefaultChannel")
            .addModifiers(KModifier.OPEN)
            .returns(String::class)
            .addCode("return \"DefaultChannel\"\n")
            .build()
    }

    private fun buildResolveChannelByBodyType(): FunSpec {
        return FunSpec.builder("resolveChannelByBodyType")
            .addModifiers(KModifier.OPEN)
            .addParameter("value", String::class)
            .returns(String::class)
            .addCode("return value.replace(\".\", \"-\")\n")
            .build()
    }

    private fun buildLookupReplyChannel(): FunSpec {
        return FunSpec.builder("lookupReplyChannel")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("message", Framework.Message)
            .returns(String::class)
            .addCode("return lookupChannel(message) + \"-reply\"\n")
            .build()
    }

    private fun buildLookupChannel(messagings: List<MessagingSetting>): FunSpec {
        val code = CodeBlock.builder()
        code.add("val bodyType = message.attributes[\"bodyType\"]\n")
            .beginControlFlow("if (null === bodyType)")
            .add("return getDefaultChannel()\n")
            .endControlFlow()
            .add("\n")

        code.add("val value = bodyType.stringValue\n")
            .beginControlFlow("if (null === value)")
            .add("return getDefaultChannel()\n")
            .endControlFlow()
            .add("\n")

        if (messagings.isEmpty()) {
            code.add("return resolveChannelByBodyType(value)\n")
        } else {
            code.beginControlFlow("return when (value)")
            messagings.forEach {
                code.add("%S -> %S\n", it.contract.fullName(), it.channel)
            }
            code.add("\n")
            code.add("else -> resolveChannelByBodyType(value)\n")
            code.endControlFlow()
        }

        return FunSpec.builder("lookupChannel")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("message", Framework.Message)
            .returns(String::class)
            .addCode(code.build())
            .build()
    }
}