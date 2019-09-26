package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.HandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo

class MockableServiceBusTestGenerator : AbstractMockableBusGeneratorTestGenerator() {
    private val data = mutableMapOf<String, ClassInfo>()

    override val abstractMockableBus = Framework.AbstractMockableServiceBus

    override val busType = Framework.ServiceBus

    override val busResolverInput = Framework.Request.parameterizedBy(TypeVariableName("*"))

    override val busResolverOutput = Framework.RequestHandler.parameterizedBy(
        TypeVariableName.invoke("*"), TypeVariableName.invoke("*")
    )

    override val busInputName = "request"

    override val guessKClassFunctionName = "guessRequestKClassByInstance"

    override val guessKClassOutTypeName = "out Request<*>"

    override fun findTarget(settings: GeneratorSettings, namespace: String?): ClassInfo {
        return Utility.findMockableRequestBusTarget(settings.requestHandlers, namespace)
    }

    override fun getContracts(settings: GeneratorSettings): List<ClassInfo> {
        val result = mutableListOf<ClassInfo>()
        settings.requestHandlers.forEach {
            if (!result.contains(it.request)) {
                result.add(it.request)
                data[it.request.fullName()] = it.response
            }
        }
        return result
    }

    override fun getHandlers(settings: GeneratorSettings): List<HandlerSetting> {
        return settings.requestHandlers
    }

    override fun findWhenProcessingReturnsType(contract: ClassInfo): TypeName {
        return Framework.ServiceBusCallFakeBuilderStart.parameterizedBy(
            contract.toClassName(),
            data[contract.fullName()]!!.toClassName()
        )
    }

    override fun findShouldProcessReturnsType(contract: ClassInfo): TypeName {
        return Framework.BusCalledWithBuilderStart.parameterizedBy(
            contract.toClassName()
        )
    }
}