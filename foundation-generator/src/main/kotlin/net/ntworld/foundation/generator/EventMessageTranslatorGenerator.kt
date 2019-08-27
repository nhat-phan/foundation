package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

object EventMessageTranslatorGenerator {
    fun generate(settings: EventSettings): GeneratedFile {
        val target = Utility.findEventMessageTranslatorTarget(settings)
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    internal fun buildFile(settings: EventSettings, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(settings, target))

        return file.build()
    }

    internal fun buildClass(settings: EventSettings, target: ClassInfo): TypeSpec {
        val type = TypeSpec.objectBuilder(target.className)
            .addSuperinterface(
                Framework.MessageTranslator.parameterizedBy(
                    settings.event.toClassName()
                )
            )
            .addProperty(
                PropertySpec.builder("json", Framework.Json)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("Json(%T.Stable.copy(strictMode = false))", Framework.JsonConfiguration)
                    .build()
            )

        buildMakeFunctionIfNeeded(settings, type)
        buildCanConvertFunction(settings, type)
        buildFromMessageFunction(settings, type)
        buildToMessageFunction(settings, type)
        return type.build()
    }

    internal fun buildMakeFunctionIfNeeded(settings: EventSettings, type: TypeSpec.Builder) {
        if (settings.event == settings.implementation) {
            return
        }

        if (settings.hasSecondConstructor) {
            type.addFunction(
                FunSpec.builder("make")
                    .addModifiers(KModifier.PRIVATE)
                    .addParameter("event", settings.implementation.toClassName())
                    .returns(settings.implementation.toClassName())
                    .addStatement("return %T(event)", settings.implementation.toClassName())
                    .build()
            )
            return
        }

        val code = CodeBlock.builder()
        code.beginControlFlow("if (event is %T)", settings.implementation.toClassName())
        code.add("return event\n")
        code.endControlFlow()
        code.add("return %T(\n", settings.implementation.toClassName())
        code.indent()
        settings.fields.forEachIndexed { index, field ->
            code.add("%L = event.%L", field.name, field.name)
            if (index != settings.fields.lastIndex) {
                code.add(",")
            }
            code.add("\n")
        }
        code.unindent()
        code.add(")\n")

        type.addFunction(
            FunSpec.builder("make")
                .addModifiers(KModifier.PRIVATE)
                .addParameter("event", settings.implementation.toClassName())
                .returns(settings.implementation.toClassName())
                .addCode(code.build())
                .build()
        )
    }

    internal fun buildCanConvertFunction(settings: EventSettings, type: TypeSpec.Builder) {
        val code = CodeBlock.builder()
        code.add("val bodyType = message.attributes[\"bodyType\"]\n")
        code.add(
            "return null !== bodyType && bodyType.stringValue == %S\n",
            settings.event.fullName()
        )

        type.addFunction(
            FunSpec.builder("canConvert")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("message", Framework.Message)
                .returns(Boolean::class)
                .addCode(code.build())
                .build()
        )
    }

    internal fun buildFromMessageFunction(settings: EventSettings, type: TypeSpec.Builder) {
        type.addFunction(
            FunSpec.builder("fromMessage")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("message", Framework.Message)
                .returns(settings.implementation.toClassName())
                .addStatement("return json.parse(%T.serializer(), message.body)", settings.implementation.toClassName())
                .build()
        )
    }

    internal fun buildToMessageFunction(settings: EventSettings, type: TypeSpec.Builder) {
        val code = CodeBlock.builder()
        code.add("val attributes = mapOf<String, %T>(\n", Framework.MessageAttribute)
        code.indent()

        code
            .add("\"bodyType\" to %T.createStringAttribute(\n", Framework.MessageUtility)
            .indent()
            .add("%S\n", settings.event.fullName())
            .unindent()
            .add("),\n")

        code.add("\"implementationType\" to %T.createStringAttribute(\n", Framework.MessageUtility)
            .indent()
            .add("%S\n", settings.implementation.fullName())
            .unindent()
            .add(")\n")

        code.unindent()
        code.add(")\n")
        code.add("\n")

        code.add("return MessageUtility.createMessage(\n")
        code.indent()

        if (settings.event == settings.implementation) {
            code.add("json.stringify(%T.serializer(), input),\n", settings.implementation.toClassName())
        } else {
            code.add("json.stringify(%T.serializer(), make(input)),\n", settings.implementation.toClassName())
        }
        code.add("attributes\n")

        code.unindent()
        code.add(")\n")

        type.addFunction(
            FunSpec.builder("toMessage")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("input", settings.event.toClassName())
                .returns(Framework.Message)
                .addCode(code.build())
                .build()
        )
    }
}