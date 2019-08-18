package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.EventField

object EventConverterGenerator {
    fun generate(settings: EventSettings): GeneratedFile {
        val target = Utility.findEventConverterTarget(settings)
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    internal fun buildFile(settings: EventSettings, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        Framework.addFileHeader(file, this::class.qualifiedName)
        file.addType(buildClass(settings, target))

        return file.build()
    }

    internal fun buildClass(settings: EventSettings, target: ClassInfo): TypeSpec {
        return TypeSpec.classBuilder(target.className)
            .addSuperinterface(
                Framework.EventConverter.parameterizedBy(
                    ClassName(settings.event.packageName, settings.event.className)
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
            .addFunction(buildToEventDataFunction(settings))
            .addFunction(buildFromEventDataFunction(settings))
            .build()
    }

    internal fun buildToEventDataFunction(settings: EventSettings): FunSpec {
        val eventDataTarget = Utility.findEventDataTarget(settings)
        val codeBlock = CodeBlock.builder()

        codeBlock.add("%T\n", Framework.Converter)
        codeBlock.indent()
        settings.fields.forEach {
            codeBlock.add(getWriteEventToEventDataLine(it)).add("\n")
        }
        codeBlock.unindent()
        codeBlock.add("\n")

        return FunSpec.builder("toEventData")
            .addModifiers(KModifier.OVERRIDE)
            .returns(ClassName(eventDataTarget.packageName, eventDataTarget.className))
            .addParameter("streamId", String::class)
            .addParameter("streamType", String::class)
            .addParameter("version", Int::class)
            .addParameter("event", ClassName(settings.event.packageName, settings.event.className))
            .addCode(getCreateEventDataCodeBlock(eventDataTarget))
            .addCode(codeBlock.build())
            .addCode("return data\n")
            .build()
    }

    internal fun buildFromEventDataFunction(settings: EventSettings): FunSpec {
        val codeBlock = CodeBlock.builder()
        codeBlock.add("return %T(\n", ClassName(settings.event.packageName, settings.event.className))
        codeBlock.indent()

        settings.fields.forEachIndexed { index, field ->
            codeBlock.add(getReadEventFromEventDataLine(field))

            if (index != settings.fields.lastIndex) {
                codeBlock.add(",")
            }
            codeBlock.add("\n")
        }

        codeBlock.unindent()
        codeBlock.add(")\n")

        return FunSpec.builder("fromEventData")
            .addModifiers(KModifier.OVERRIDE)
            .returns(ClassName(settings.event.packageName, settings.event.className))
            .addParameter("data", Framework.EventData)
            .addCode(codeBlock.build())
            .build()
    }

    internal fun getCreateEventDataCodeBlock(eventDataTarget: ClassInfo): CodeBlock {
        val codeBlock = CodeBlock.builder()
        codeBlock.add("val data = %T(\n", ClassName(eventDataTarget.packageName, eventDataTarget.className))
            .indent()
            .add("id = infrastructure.root.idGeneratorOf().generate(),\n")
            .add("streamId = streamId,\n")
            .add("streamType = streamType,\n")
            .add("version = version,\n")
            .add("data = mutableMapOf(),\n")
            .add("metadata = mutableMapOf()\n")
            .unindent()
            .add(")\n")
            .add("\n")
        return codeBlock.build()
    }

    internal fun getWriteEventToEventDataLine(field: EventField): CodeBlock {
        // metadata field, don't care about the other settings
        if (field.metadata) {
            return CodeBlock.of(".writeMetadata(data, %S, event.%L)", field.name, field.name)
        }

        // data field, no encrypted
        if (!field.encrypted) {
            return CodeBlock.of(".write(data, %S, event.%L)", field.name, field.name)
        }

        // encrypted
        return CodeBlock.of(
            ".encrypt(data, %S, event.%L, infrastructure.root)",
            field.name,
            field.name
        )
    }

    internal fun getReadEventFromEventDataLine(field: EventField): CodeBlock {
        // metadata field, don't care about the other settings
        if (field.metadata) {
            return CodeBlock.of("%L = %T.readMetadata(data, %S)", field.name, Framework.Converter, field.name)
        }

        // data field, no encrypted
        if (!field.encrypted) {
            return CodeBlock.of("%L = %T.read(data, %S)", field.name, Framework.Converter, field.name)
        }

        // encrypted field but no faked data
        if (field.faked.isEmpty()) {
            return CodeBlock.of(
                "%L = %T.decrypt(data, %S, null, infrastructure.root)",
                field.name,
                Framework.Converter,
                field.name
            )
        }

        // encrypted field with faked data
        return CodeBlock.of(
            "%L = %T.decrypt(data, %S, %S, infrastructure.root)",
            field.name,
            Framework.Converter,
            field.name,
            field.faked
        )
    }
}