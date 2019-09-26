package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.HandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo

class MockableQueryBusTestGenerator : AbstractMockableBusGeneratorTestGenerator() {
    private val data = mutableMapOf<String, ClassInfo>()

    override val abstractMockableBus = Framework.AbstractMockableQueryBus

    override val busType = Framework.QueryBus

    override val busResolverInput = Framework.Query.parameterizedBy(TypeVariableName("*"))

    override val busResolverOutput = Framework.QueryHandler.parameterizedBy(
        TypeVariableName.invoke("*"), TypeVariableName.invoke("*")
    )

    override val busInputName = "query"

    override val guessKClassFunctionName = "guessQueryKClassByInstance"

    override val guessKClassOutTypeName = "out Query<*>"

    override fun findTarget(settings: GeneratorSettings, namespace: String?): ClassInfo {
        return Utility.findMockableQueryBusTarget(settings.queryHandlers, namespace)
    }

    override fun getContracts(settings: GeneratorSettings): List<ClassInfo> {
        val result = mutableListOf<ClassInfo>()
        settings.queryHandlers.forEach {
            if (!result.contains(it.query)) {
                result.add(it.query)
                data[it.query.fullName()] = it.queryResult
            }
        }
        return result
    }

    override fun getHandlers(settings: GeneratorSettings): List<HandlerSetting> {
        return settings.queryHandlers
    }

    override fun findWhenProcessingReturnsType(contract: ClassInfo): TypeName {
        return Framework.QueryBusCallFakeBuilderStart.parameterizedBy(
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