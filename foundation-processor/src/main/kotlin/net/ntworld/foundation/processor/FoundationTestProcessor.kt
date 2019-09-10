package net.ntworld.foundation.processor

import net.ntworld.foundation.generator.util.ContractReader
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.Platform
import net.ntworld.foundation.generator.main.ContractImplementationMainGenerator
import net.ntworld.foundation.generator.main.InfrastructureProviderMainGenerator
import net.ntworld.foundation.generator.test.ContractFactoryTestGenerator
import net.ntworld.foundation.generator.test.UtilityTestGenerator
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.util.FrameworkProcessor
import net.ntworld.foundation.processor.util.ProcessorOutput
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes(
    "kotlin.test.Test",
    "org.junit.Test"
)
@SupportedOptions(
    FrameworkProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME,
    FrameworkProcessor.MODE_OPTION_NAME
)
class FoundationTestProcessor : AbstractProcessor() {
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val settings = ProcessorOutput.readSettingsFile(processingEnv, true)
        val global = InfrastructureProviderMainGenerator().findTarget(settings)
        generateContractFactory(settings, global)

        return true
    }

    private fun generateContractFactory(settings: GeneratorSettings, global: ClassInfo) {
        val reader = ContractReader(
            contractSettings = settings.contracts,
            fakedAnnotationSettings = settings.fakedAnnotations,
            fakedPropertySettings = settings.fakedProperties
        )

        val utilityTestGenerator = UtilityTestGenerator(Platform.Jvm)
        val factoryTestGenerator = ContractFactoryTestGenerator(Platform.Jvm)
        val implementations = mutableMapOf<String, String>()
        settings.implementations.forEach {
            implementations[it.contract.fullName()] = it.name
        }

        settings.contracts.forEach {
            if (it.collectedBy != ContractCollector.COLLECTED_BY_KAPT || implementations.containsKey(it.name)) {
                return@forEach
            }

            val properties = reader.findPropertiesOfContract(it.name)
            if (null !== properties) {
                val implFile = ContractImplementationMainGenerator.findImplementationTarget(it)
                utilityTestGenerator.add(it.contract)
                factoryTestGenerator.add(it.contract, implFile)
            }
        }
        val utilityGeneratedFile = utilityTestGenerator.generate(global.packageName)
        ProcessorOutput.writeGeneratedFile(processingEnv, utilityGeneratedFile)
        ProcessorOutput.writeGeneratedFile(
            processingEnv,
            factoryTestGenerator.generate(
                settings,
                utilityGeneratedFile.target,
                global.packageName
            )
        )
    }
}