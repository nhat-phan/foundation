package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.internal.AbstractFactoryGenerator
import net.ntworld.foundation.generator.internal.AbstractFactoryWithEventSourcedGenerator
import net.ntworld.foundation.generator.internal.WrapperFactoryWithEventSourcedGenerator
import net.ntworld.foundation.generator.setting.AggregateFactorySetting
import net.ntworld.foundation.generator.type.ClassInfo

object AggregateFactoryGenerator {
    fun generate(setting: AggregateFactorySetting): GeneratedFile {
        val target = Utility.findAggregateFactoryTarget(setting)
        val file = buildFile(setting, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    internal fun buildFile(setting: AggregateFactorySetting, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(setting, target))

        return file.build()
    }

    internal fun buildClass(setting: AggregateFactorySetting, target: ClassInfo): TypeSpec {
        val type = TypeSpec.classBuilder(target.className)
            .addSuperinterface(
                Framework.AggregateFactory.parameterizedBy(
                    setting.aggregate.toClassName(),
                    setting.state.toClassName()
                )
            )
        when(getFactoryType(setting)) {
            AggregateFactorySetting.Type.ABSTRACT_FACTORY -> {
                AbstractFactoryGenerator.buildType(type, setting)
            }
            AggregateFactorySetting.Type.WRAPPER_FACTORY_WITH_EVENT_SOURCED -> {
                WrapperFactoryWithEventSourcedGenerator.buildType(type, setting)
            }
            AggregateFactorySetting.Type.ABSTRACT_FACTORY_WITH_EVENT_SOURCED -> {
                AbstractFactoryWithEventSourcedGenerator.buildType(type, setting)
            }
        }

        return type.build()
    }

    private fun getFactoryType(setting: AggregateFactorySetting): AggregateFactorySetting.Type {
        if (setting.isAbstract && !setting.isEventSourced) {
            return AggregateFactorySetting.Type.ABSTRACT_FACTORY
        }
        if (!setting.isAbstract && setting.isEventSourced) {
            return AggregateFactorySetting.Type.WRAPPER_FACTORY_WITH_EVENT_SOURCED
        }
        if (setting.isAbstract && setting.isEventSourced) {
            return AggregateFactorySetting.Type.ABSTRACT_FACTORY_WITH_EVENT_SOURCED
        }
        throw Exception("Invalid AggregateFactorySetting")
    }
}