package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.setting.EventDataSettings
import net.ntworld.foundation.generator.type.ClassInfo
import java.io.File
import javax.annotation.processing.Filer
import kotlin.reflect.KClass

object EventDataMessageConverterGenerator {
    fun generate(settings: EventDataSettings, out: Appendable) {
        buildFile(settings).writeTo(out)
    }

    fun generate(settings: EventDataSettings, out: Filer) {
        buildFile(settings).writeTo(out)
    }

    fun generate(settings: EventDataSettings, out: File) {
        buildFile(settings).writeTo(out)
    }

    fun buildFile(settings: EventDataSettings): FileSpec {
        val target = Utility.findEventDataTarget(settings)
        val converterTarget = Utility.findEventDataMessageConverterTarget(settings)

        val file = FileSpec.builder(converterTarget.packageName, converterTarget.className)
        Framework.addFileHeader(file, this::class.qualifiedName)
        file.addType(buildClass(settings, target, converterTarget))

        return file.build()
    }

    internal fun buildClass(settings: EventDataSettings, target: ClassInfo, converterTarget: ClassInfo): TypeSpec {
        return TypeSpec.objectBuilder(converterTarget.className)
            .addSuperinterface(
                Framework.MessageConverter.parameterizedBy(
                    ClassName(target.packageName, target.className)
                )
            )
            .addFunction(buildFromMessageFunction(target))
            .addFunction(buildToMessageFunction(target))
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
                Framework.EventMessageConverter
            )
            .build()
    }

    internal fun buildCanConvertFunction(settings: EventDataSettings): FunSpec {
        return FunSpec.builder("canConvert")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("message", Framework.Message)
            .addStatement(
                "return %T.canConvert(message, %S, %L)",
                Framework.EventMessageConverter,
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
            .addStatement("""data = message.attributes["data"] as Map<String, Any>,""")
            .addStatement("""metadata = message.attributes["metadata"] as Map<String, Any>""")
            .unindent()
            .add(")\n")
            .build()
        return FunSpec.builder("fromMessage")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("message", Framework.Message)
            .addCode(codeBlock)
            .build()
    }
}