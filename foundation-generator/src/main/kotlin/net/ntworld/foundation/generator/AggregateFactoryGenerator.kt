package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.internal.AbstractFactoryGenerator
import net.ntworld.foundation.generator.internal.AbstractFactoryWithEventSourcedGenerator
import net.ntworld.foundation.generator.internal.WrapperFactoryWithEventSourcedGenerator
import net.ntworld.foundation.generator.setting.AggregateFactorySettings
import net.ntworld.foundation.generator.type.ClassInfo

object AggregateFactoryGenerator {
    fun generate(settings: AggregateFactorySettings): GeneratedFile {
        val target = Utility.findAggregateFactoryTarget(settings)
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    internal fun buildFile(settings: AggregateFactorySettings, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(settings, target))

        return file.build()
    }

    internal fun buildClass(settings: AggregateFactorySettings, target: ClassInfo): TypeSpec {
        val type = TypeSpec.classBuilder(target.className)
            .addSuperinterface(
                Framework.AggregateFactory.parameterizedBy(
                    settings.aggregate.toClassName(),
                    settings.state.toClassName()
                )
            )
        when(getFactoryType(settings)) {
            AggregateFactorySettings.Type.ABSTRACT_FACTORY -> {
                AbstractFactoryGenerator.buildType(type, settings)
            }
            AggregateFactorySettings.Type.WRAPPER_FACTORY_WITH_EVENT_SOURCED -> {
                WrapperFactoryWithEventSourcedGenerator.buildType(type, settings)
            }
            AggregateFactorySettings.Type.ABSTRACT_FACTORY_WITH_EVENT_SOURCED -> {
                AbstractFactoryWithEventSourcedGenerator.buildType(type, settings)
            }
        }

        return type.build()
    }

    private fun getFactoryType(settings: AggregateFactorySettings): AggregateFactorySettings.Type {
        if (settings.isAbstract && !settings.isEventSourced) {
            return AggregateFactorySettings.Type.ABSTRACT_FACTORY
        }
        if (!settings.isAbstract && settings.isEventSourced) {
            return AggregateFactorySettings.Type.WRAPPER_FACTORY_WITH_EVENT_SOURCED
        }
        if (settings.isAbstract && settings.isEventSourced) {
            return AggregateFactorySettings.Type.ABSTRACT_FACTORY_WITH_EVENT_SOURCED
        }
        throw Exception("Invalid AggregateFactorySettings")
    }
}