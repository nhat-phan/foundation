package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorOutput
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.EventSourcingSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.EventField

object EventConverterMainGenerator {
    fun generate(setting: EventSourcingSetting): GeneratedFile {
        val target = Utility.findEventConverterTarget(setting)
        val file = buildFile(setting, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return GeneratedFile.makeMainFile(target, stringBuffer.toString())
    }

    internal fun buildFile(setting: EventSourcingSetting, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(setting, target))

        return file.build()
    }

    internal fun buildClass(setting: EventSourcingSetting, target: ClassInfo): TypeSpec {
        return TypeSpec.classBuilder(target.className)
            .addSuperinterface(
                Framework.EventConverter.parameterizedBy(
                    setting.event.toClassName()
                )
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("infrastructure", Framework.Infrastructure)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("infrastructure", Framework.Infrastructure)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("infrastructure")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("json", Framework.Json)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("Json(%T.Stable.copy(strictMode = false))",
                        Framework.JsonConfiguration
                    )
                    .build()
            )
            .addType(buildCompanionObject(setting))
            .addFunction(
                buildToEventEntityFunction(
                    setting
                )
            )
            .addFunction(
                buildFromEventEntityFunction(
                    setting
                )
            )
            .build()
    }

    internal fun buildToEventEntityFunction(setting: EventSourcingSetting): FunSpec {
        val eventEntityTarget = Utility.findEventEntityTarget(setting)
        val code = CodeBlock.builder()

        code.add("val raw = json.stringify(%T.serializer(), event)\n", setting.implementation.toClassName())
        code.add(
            "val processed = %T.processRawJson(infrastructure, json, fields, raw)\n",
            Framework.EventEntityConverterUtility
        )
        code.add("return %T(\n", eventEntityTarget.toClassName())
        code.indent()
        code.add("id = infrastructure.root.idGeneratorOf(%T::class).generate(),\n", setting.event.toClassName())
        code.add("streamId = streamId,\n")
        code.add("streamType = streamType,\n")
        code.add("version = version,\n")
        code.add("data = processed.data,\n")
        code.add("metadata = processed.metadata\n")
        code.unindent()
        code.add(")\n")

        return FunSpec.builder("toEventEntity")
            .addModifiers(KModifier.OVERRIDE)
            .returns(eventEntityTarget.toClassName())
            .addParameter("streamId", String::class)
            .addParameter("streamType", String::class)
            .addParameter("version", Int::class)
            .addParameter("event", setting.event.toClassName())
            .addCode(code.build())
            .build()
    }

    internal fun buildFromEventEntityFunction(setting: EventSourcingSetting): FunSpec {
        val code = CodeBlock.builder()
        code.add(
            "val raw = %T.rebuildRawJson(infrastructure, json, fields, eventEntity.data, eventEntity.metadata)\n",
            Framework.EventEntityConverterUtility
        )
        code.add("return json.parse(%T.serializer(), raw)\n", setting.implementation.toClassName())

        return FunSpec.builder("fromEventEntity")
            .addModifiers(KModifier.OVERRIDE)
            .returns(ClassName(setting.event.packageName, setting.implementation.className))
            .addParameter("eventEntity", Framework.EventEntity)
            .addCode(code.build())
            .build()
    }

    internal fun buildCompanionObject(setting: EventSourcingSetting): TypeSpec {
        val code = CodeBlock.builder()
        code.add("\nmapOf(\n")
        code.indent()
        setting.fields.forEachIndexed { index, it ->
            code.add("%S to ", it.name)
            code.add(buildFieldSetting(it))

            if (index != setting.fields.lastIndex) {
                code.add(",")
            }
            code.add("\n")
        }
        code.unindent()
        code.add(")\n")

        return TypeSpec.companionObjectBuilder()
            .addProperty(
                PropertySpec.builder(
                    "fields",
                    ClassName("kotlin.collections", "Map").parameterizedBy(
                        ClassName("kotlin", "String"),
                        Framework.EventEntityConverterUtilitySetting
                    )
                )
                    .initializer(code.build())
                    .build()
            )
            .build()
    }

    internal fun buildFieldSetting(field: EventField): CodeBlock {
        if (field.metadata) {
            return CodeBlock.of("%T(metadata = true)",
                Framework.EventEntityConverterUtilitySetting
            )
        }

        if (field.encrypted) {
            return CodeBlock.of("%T(encrypted = true, faked = %S)",
                Framework.EventEntityConverterUtilitySetting, field.faked)
        }

        return CodeBlock.of("%T()", Framework.EventEntityConverterUtilitySetting)
    }
}