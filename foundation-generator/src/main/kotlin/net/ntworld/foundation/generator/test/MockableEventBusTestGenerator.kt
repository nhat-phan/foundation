package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.HandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo

class MockableEventBusTestGenerator : AbstractMockableBusGeneratorTestGenerator() {
    override val abstractMockableBus = Framework.AbstractMockableEventBus

    override val busType = Framework.EventBus

    override val busResolverInput = Framework.Event

    override val busResolverOutput = ClassName(packageName = "kotlin", simpleName = "Array")
        .parameterizedBy(
            Framework.EventHandler.parameterizedBy(TypeVariableName.invoke("*"))
        )

    override val busInputName = "event"

    override val guessKClassFunctionName = "guessEventKClassByInstance"

    override val guessKClassOutTypeName = "out Event"

    override fun findTarget(settings: GeneratorSettings, namespace: String?): ClassInfo {
        return Utility.findMockableEventBusTarget(settings.eventHandlers, namespace)
    }

    override fun getContracts(settings: GeneratorSettings): List<ClassInfo> {
        val result = mutableListOf<ClassInfo>()
        settings.eventHandlers.forEach {
            if (!result.contains(it.event)) {
                result.add(it.event)
            }
        }
        return result
    }

    override fun getHandlers(settings: GeneratorSettings): List<HandlerSetting> {
        return settings.eventHandlers
    }

    override fun findWhenProcessingReturnsType(contract: ClassInfo): TypeName {
        return Framework.EventBusCallFakeBuilderStart.parameterizedBy(
            contract.toClassName()
        )
    }

    override fun findShouldProcessReturnsType(contract: ClassInfo): TypeName {
        return Framework.BusCalledWithBuilderStart.parameterizedBy(
            contract.toClassName()
        )
    }

    override fun buildHelperFunctionsForContractHaveCompanion(type: TypeSpec.Builder, contract: ClassInfo) {
        super.buildHelperFunctionsForContractHaveCompanion(type, contract)

        val whenPublishing = FunSpec.builder("whenPublishing")
            .addModifiers(KModifier.INFIX)
            .addAnnotation(Framework.TestDslMock)
            .addAnnotation(
                AnnotationSpec.builder(Suppress::class)
                    .addMember("%S", "UNUSED_PARAMETER")
                    .build()
            )
            .returns(findWhenProcessingReturnsType(contract))
            .addParameter(busInputName, contract.toClassName().nestedClass("Companion"))
            .addCode("return whenPublishing(%T::class)\n", contract.toClassName())

        val shouldPublish = FunSpec.builder("shouldPublish")
            .addModifiers(KModifier.INFIX)
            .addAnnotation(Framework.TestDslVerify)
            .addAnnotation(
                AnnotationSpec.builder(Suppress::class)
                    .addMember("%S", "UNUSED_PARAMETER")
                    .build()
            )
            .returns(findShouldProcessReturnsType(contract))
            .addParameter(busInputName, contract.toClassName().nestedClass("Companion"))
            .addCode("return shouldPublish(%T::class)\n", contract.toClassName())

        type.addFunction(whenPublishing.build())
        type.addFunction(shouldPublish.build())
    }
}