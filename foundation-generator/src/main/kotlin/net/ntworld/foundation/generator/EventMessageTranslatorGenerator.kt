package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.setting.EventSourcingSetting
import net.ntworld.foundation.generator.type.ClassInfo
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

object EventMessageTranslatorGenerator {
    fun generate(setting: EventSourcingSetting): GeneratedFile {
        val target = Utility.findEventMessageTranslatorTarget(setting)
        val file = buildFile(setting, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    internal fun buildFile(setting: EventSourcingSetting, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(setting, target))

        return file.build()
    }

    internal fun buildClass(setting: EventSourcingSetting, target: ClassInfo): TypeSpec {
        val type = TypeSpec.objectBuilder(target.className)
            .addSuperinterface(
                Framework.MessageTranslator.parameterizedBy(
                    setting.event.toClassName()
                )
            )
            .addProperty(
                PropertySpec.builder("json", Framework.Json)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("Json(%T.Stable.copy(strictMode = false))", Framework.JsonConfiguration)
                    .build()
            )

        buildMakeFunctionIfNeeded(setting, type)
        buildCanConvertFunction(setting, type)
        buildFromMessageFunction(setting, type)
        buildToMessageFunction(setting, type)
        return type.build()
    }

    internal fun buildMakeFunctionIfNeeded(setting: EventSourcingSetting, type: TypeSpec.Builder) {
        if (setting.event == setting.implementation) {
            return
        }

        if (setting.hasSecondConstructor) {
            type.addFunction(
                FunSpec.builder("make")
                    .addModifiers(KModifier.PRIVATE)
                    .addParameter("event", setting.implementation.toClassName())
                    .returns(setting.implementation.toClassName())
                    .addStatement("return %T(event)", setting.implementation.toClassName())
                    .build()
            )
            return
        }

        val code = CodeBlock.builder()
        code.beginControlFlow("if (event is %T)", setting.implementation.toClassName())
        code.add("return event\n")
        code.endControlFlow()
        code.add("return %T(\n", setting.implementation.toClassName())
        code.indent()
        setting.fields.forEachIndexed { index, field ->
            code.add("%L = event.%L", field.name, field.name)
            if (index != setting.fields.lastIndex) {
                code.add(",")
            }
            code.add("\n")
        }
        code.unindent()
        code.add(")\n")

        type.addFunction(
            FunSpec.builder("make")
                .addModifiers(KModifier.PRIVATE)
                .addParameter("event", setting.implementation.toClassName())
                .returns(setting.implementation.toClassName())
                .addCode(code.build())
                .build()
        )
    }

    internal fun buildCanConvertFunction(setting: EventSourcingSetting, type: TypeSpec.Builder) {
        val code = CodeBlock.builder()
        code.add("val bodyType = message.attributes[\"bodyType\"]\n")
        code.add(
            "return null !== bodyType && bodyType.stringValue == %S\n",
            setting.event.fullName()
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

    internal fun buildFromMessageFunction(setting: EventSourcingSetting, type: TypeSpec.Builder) {
        type.addFunction(
            FunSpec.builder("fromMessage")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("message", Framework.Message)
                .returns(setting.implementation.toClassName())
                .addStatement("return json.parse(%T.serializer(), message.body)", setting.implementation.toClassName())
                .build()
        )
    }

    internal fun buildToMessageFunction(setting: EventSourcingSetting, type: TypeSpec.Builder) {
        val code = CodeBlock.builder()
        code.add("val attributes = mapOf<String, %T>(\n", Framework.MessageAttribute)
        code.indent()

        code
            .add("\"bodyType\" to %T.createStringAttribute(\n", Framework.MessageUtility)
            .indent()
            .add("%S\n", setting.event.fullName())
            .unindent()
            .add("),\n")

        code.add("\"implementationType\" to %T.createStringAttribute(\n", Framework.MessageUtility)
            .indent()
            .add("%S\n", setting.implementation.fullName())
            .unindent()
            .add(")\n")

        code.unindent()
        code.add(")\n")
        code.add("\n")

        code.add("return MessageUtility.createMessage(\n")
        code.indent()

        if (setting.event == setting.implementation) {
            code.add("json.stringify(%T.serializer(), input),\n", setting.implementation.toClassName())
        } else {
            code.add("json.stringify(%T.serializer(), make(input)),\n", setting.implementation.toClassName())
        }
        code.add("attributes\n")

        code.unindent()
        code.add(")\n")

        type.addFunction(
            FunSpec.builder("toMessage")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("input", setting.event.toClassName())
                .returns(Framework.Message)
                .addCode(code.build())
                .build()
        )
    }
}