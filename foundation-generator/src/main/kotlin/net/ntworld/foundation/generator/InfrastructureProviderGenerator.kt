package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo

class InfrastructureProviderGenerator {
    private val variableNames = mutableListOf<String>()

    fun generate(settings: GeneratorSettings): GeneratedFile {
        val target = Utility.findInfrastructureProviderTarget(settings)
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, file.toString())
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

        val events = settings.events.sortedWith(EventSettings.Companion.comparator)
        events.forEach { buildRegisterCodeForEvent(builder, init, it) }

        builder.addInitializerBlock(init.build())

        return builder.build()
    }

    internal fun buildRegisterCodeForEvent(type: TypeSpec.Builder, init: CodeBlock.Builder, settings: EventSettings) {
        val eventConverter = Utility.findEventConverterTarget(settings)
        val eventConverterClass = ClassName(eventConverter.packageName, eventConverter.className)
        val eventClass = ClassName(settings.event.packageName, settings.event.className)

        val eventEntity = Utility.findEventEntityTarget(settings)
        val eventEntityClass = ClassName(eventEntity.packageName, eventEntity.className)

        val eventConverterVariableName = findVariableNames(eventConverter.packageName, eventConverter.className)
        type.addProperty(
            PropertySpec.builder(eventConverterVariableName, eventConverterClass)
                .addModifiers(KModifier.PRIVATE)
                .initializer(CodeBlock.of("%T(this)", eventConverterClass))
                .build()
        )

        init.indent()
        init.add(
            "registerEventConverter(%T::class, this.%L)\n", eventClass, eventConverterVariableName
        )
        init.add("registerEventConverter(%S, %L, %L)\n", settings.type, settings.variant, eventConverterVariableName)
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