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
            .addFunction(buildFromMessageFunction(eventData))
            .addFunction(buildToMessageFunction(eventData))
            .addFunction(buildCanConvertFunction(settings))
            .build()
    }

    internal fun buildMessageConverterOfFunction(target: ClassInfo): FunSpec {
        return FunSpec.builder("messageConverterOf")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("type", KClass::class)
            .returns(
                Framework.MessageConverter.parameterizedBy(
                    ClassName(target.packageName, target.className)
                )
            )
            .build()
    }

    internal fun buildToMessageFunction(target: ClassInfo): FunSpec {
        return FunSpec.builder("toMessage")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("input", ClassName(target.packageName, target.className))
            .returns(Framework.Message)
            .addStatement(
                "return %T.toMessage(input)",
                Framework.EventMessageConverterUtility
            )
            .build()
    }

    internal fun buildCanConvertFunction(settings: EventSettings): FunSpec {
        return FunSpec.builder("canConvert")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("message", Framework.Message)
            .addStatement(
                "return %T.canConvert(message, %S, %L)",
                Framework.EventMessageConverterUtility,
                settings.type,
                settings.variant
            )
            .build()
    }

    internal fun buildFromMessageFunction(target: ClassInfo): FunSpec {
        val codeBlock = CodeBlock.builder()
            .add(
                "return %T(\n", ClassName(target.packageName, target.className)
            )
            .indent()
            .addStatement("""id = message.attributes["id"] as String,""")
            .addStatement("""streamId = message.attributes["streamId"] as String,""")
            .addStatement("""streamType = message.attributes["streamType"] as String,""")
            .addStatement("""version = message.attributes["version"] as Int,""")
            .addStatement("""data = message.attributes["data"] as String,""")
            .addStatement("""metadata = message.attributes["metadata"] as String""")
            .unindent()
            .add(")\n")
            .build()
        return FunSpec.builder("fromMessage")
            .addModifiers(KModifier.OVERRIDE)
            .addAnnotation(
                AnnotationSpec.builder(Suppress::class)
                    .addMember("%S", "UNCHECKED_CAST")
                    .build()
            )
            .addParameter("message", Framework.Message)
            .addCode(codeBlock)
            .build()
    }
}