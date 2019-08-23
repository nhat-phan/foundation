package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo
import kotlin.reflect.KClass

object EventDataMessageConverterGenerator {
    fun generate(settings: EventSettings): GeneratedFile {
        val target = Utility.findEventDataMessageConverterTarget(settings)
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    fun buildFile(settings: EventSettings, target: ClassInfo): FileSpec {
        val eventDataTarget = Utility.findEventDataTarget(settings)
        val file = FileSpec.builder(target.packageName, target.className)
        Framework.addFileHeader(file, this::class.qualifiedName)
        file.addType(buildClass(settings, eventDataTarget, target))

        return file.build()
    }

    internal fun buildClass(settings: EventSettings, eventData: ClassInfo, target: ClassInfo): TypeSpec {
        return TypeSpec.objectBuilder(target.className)
            .addSuperinterface(
                Framework.MessageConverter.parameterizedBy(
                    ClassName(eventData.packageName, eventData.className)
                )
            )
            .addProperty(
                PropertySpec.builder("json", Framework.Json)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("Json(%T.Stable)", Framework.JsonConfiguration)
                    .build()
            )
            .addFunction(buildFromMessageFunction(eventData))
            .addFunction(buildToMessageFunction(eventData))
            .addFunction(buildCanConvertFunction(settings))
            .build()
    }

    internal fun buildToMessageFunction(target: ClassInfo): FunSpec {
        val code = CodeBlock.builder()
        code.add("return %T.toMessage(\n", Framework.EventDataMessageConverterUtility)
        code.indent()
        code.indent()
        code.indent()
        code.add("json.stringify(%T.serializer(), input),\n", target.toClassName())
        code.add("input.type,\n")
        code.add("input.variant\n")
        code.unindent()
        code.add(")")
        code.unindent()
        code.unindent()
        code.add("\n")

        return FunSpec.builder("toMessage")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("input", target.toClassName())
            .returns(Framework.Message)
            .addCode(code.build())
            .build()
    }

    internal fun buildCanConvertFunction(settings: EventSettings): FunSpec {
        return FunSpec.builder("canConvert")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("message", Framework.Message)
            .addStatement(
                "return %T.canConvert(message, %S, %L)",
                Framework.EventDataMessageConverterUtility,
                settings.type,
                settings.variant
            )
            .build()
    }

    internal fun buildFromMessageFunction(target: ClassInfo): FunSpec {
        val codeBlock = CodeBlock.builder()
            .add(
                "return json.parse(%T.serializer(), message.body)\n", ClassName(target.packageName, target.className)
            )
            .build()
        return FunSpec.builder("fromMessage")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("message", Framework.Message)
            .addCode(codeBlock)
            .build()
    }
}