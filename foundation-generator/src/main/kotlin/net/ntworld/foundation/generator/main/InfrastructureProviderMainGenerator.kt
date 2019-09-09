package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.EventSourcingSetting
import net.ntworld.foundation.generator.type.ClassInfo

class InfrastructureProviderMainGenerator {
    private val variableNames = mutableListOf<String>()

    fun findTarget(settings: GeneratorSettings): ClassInfo {
        return Utility.findInfrastructureProviderTarget(settings)
    }

    fun generate(settings: GeneratorSettings, namespace: String? = null): GeneratedFile {
        val target = Utility.findInfrastructureProviderTarget(settings, namespace)
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return GeneratedFile.makeMainFile(target, file.toString())
    }

    internal fun buildFile(settings: GeneratorSettings, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(settings, target))

        return file.build()
    }

    internal fun buildClass(settings: GeneratorSettings, target: ClassInfo): TypeSpec {
        val builder = TypeSpec.classBuilder(ClassName(target.packageName, target.className))
            .superclass(Framework.InfrastructureProvider)

        val init = CodeBlock.builder()

        val events = settings.eventSourcings.sortedWith(EventSourcingSetting.Companion.Comparision)
        events.forEach { buildRegisterCodeForEvent(builder, init, it) }

        builder.addInitializerBlock(init.build())

        return builder.build()
    }

    internal fun buildRegisterCodeForEvent(
        type: TypeSpec.Builder,
        init: CodeBlock.Builder,
        setting: EventSourcingSetting
    ) {
        val eventConverter = Utility.findEventConverterTarget(setting)
        val eventConverterClass = ClassName(eventConverter.packageName, eventConverter.className)
        val eventClass = ClassName(setting.event.packageName, setting.event.className)

        val eventConverterVariableName = findVariableNames(eventConverter.packageName, eventConverter.className)
        type.addProperty(
            PropertySpec.builder(eventConverterVariableName, eventConverterClass)
                .addModifiers(KModifier.PRIVATE)
                .initializer(CodeBlock.of("%T(this)", eventConverterClass))
                .build()
        )

        val eventMessageTranslator = Utility.findEventMessageTranslatorTarget(setting)

        init.indent()
        init.add(
            "registerEventConverter(%T::class, this.%L)\n",
            eventClass,
            eventConverterVariableName
        )
        init.add(
            "registerEventConverter(%S, %L, %L)\n",
            setting.type,
            setting.variant,
            eventConverterVariableName
        )
        init.add(
            "registerMessageTranslator(%T::class, %L)\n",
            setting.event.toClassName(),
            eventMessageTranslator.toClassName()
        )
        init.add("\n")
        init.unindent()
    }

    private fun findVariableNames(packageName: String, className: String): String {
        val simpleName = className.decapitalize()
        if (!this.variableNames.contains(simpleName)) {
            this.variableNames.add(simpleName)
            return simpleName
        }

        val complexName = packageName.replace(".", "_") + "_" + simpleName
        this.variableNames.add(complexName)
        return complexName
    }
}