package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.CommandHandlerSetting
import net.ntworld.foundation.generator.setting.HandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo

class MockableCommandBusTestGenerator : AbstractMockableBusGeneratorTestGenerator() {
    override val abstractMockableBus = Framework.AbstractMockableCommandBus

    override val busType = Framework.CommandBus

    override val busResolverInput = Framework.Command

    override val busResolverOutput = Framework.CommandHandler.parameterizedBy(TypeVariableName.invoke("*"))

    override val busInputName = "command"

    override val guessKClassFunctionName = "guessCommandKClassByInstance"

    override val guessKClassOutTypeName = "out Command"

    override fun findTarget(settings: GeneratorSettings, namespace: String?): ClassInfo {
        return Utility.findMockableCommandBusTarget(settings.commandHandlers, namespace)
    }

    override fun getContracts(settings: GeneratorSettings): List<ClassInfo> {
        val result = mutableListOf<ClassInfo>()
        settings.commandHandlers.forEach {
            if (!result.contains(it.command)) {
                result.add(it.command)
            }
        }
        return result
    }

    override fun getHandlers(settings: GeneratorSettings): List<HandlerSetting> {
        return settings.commandHandlers
    }

    override fun findWhenProcessingReturnsType(contract: ClassInfo): TypeName {
        return Framework.CommandBusCallFakeBuilderStart.parameterizedBy(
            contract.toClassName()
        )
    }

    override fun findShouldProcessReturnsType(contract: ClassInfo): TypeName {
        return Framework.BusCalledWithBuilderStart.parameterizedBy(
            contract.toClassName()
        )
    }
}