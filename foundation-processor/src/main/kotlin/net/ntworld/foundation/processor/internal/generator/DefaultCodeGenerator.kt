package net.ntworld.foundation.processor.internal.generator

import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.main.*
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.util.ContractReader
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.util.ProcessorOutput
import net.ntworld.foundation.processor.util.ProcessorSetting
import javax.annotation.processing.ProcessingEnvironment

internal class DefaultCodeGenerator : CodeGenerator {
    private data class KnownMessageTranslator(
        val contract: ClassInfo,

        val messageTranslator: ClassInfo
    )

    override val mode: ProcessorSetting.Mode = ProcessorSetting.Mode.Default

    private val messageTranslators = mutableMapOf<String, KnownMessageTranslator>()

    private fun registerMessageTranslator(contract: ClassInfo, messageTranslator: ClassInfo) {
        messageTranslators[contract.fullName()] = KnownMessageTranslator(
            contract = contract,
            messageTranslator = messageTranslator
        )
    }

    override fun generate(
        processingEnv: ProcessingEnvironment,
        processorSetting: ProcessorSetting,
        generatorSettings: GeneratorSettings
    ) {
        generateEventSourcing(processingEnv, generatorSettings)
        generateAggregateFactories(processingEnv, generatorSettings)

        val globalTarget = InfrastructureProviderMainGenerator().findTarget(generatorSettings)

        generateUnimplementedContracts(processingEnv, generatorSettings, globalTarget)
        generateLocalBuses(processingEnv, processorSetting, generatorSettings, globalTarget)
    }

    private fun generateEventSourcing(processingEnv: ProcessingEnvironment, settings: GeneratorSettings) {
        settings.eventSourcings.forEach {
            ProcessorOutput.writeGeneratedFile(processingEnv, EventEntityMainGenerator.generate(it))
            ProcessorOutput.writeGeneratedFile(processingEnv, EventConverterMainGenerator.generate(it))
            ProcessorOutput.writeGeneratedFile(processingEnv, EventMessageTranslatorMainGenerator.generate(it))
        }
    }

    private fun generateAggregateFactories(processingEnv: ProcessingEnvironment, settings: GeneratorSettings) {
        settings.aggregateFactories.forEach {
            ProcessorOutput.writeGeneratedFile(processingEnv, AggregateFactoryMainGenerator.generate(it))
        }
    }

    private fun generateUnimplementedContracts(
        processingEnv: ProcessingEnvironment,
        settings: GeneratorSettings,
        global: ClassInfo
    ) {
        val reader = ContractReader(
            contractSettings = settings.contracts,
            fakedAnnotationSettings = settings.fakedAnnotations,
            fakedPropertySettings = settings.fakedProperties
        )

        val factoryMainGenerator = ContractFactoryMainGenerator()
        val messaging = mutableMapOf<String, String>()
        settings.messagings.forEach {
            messaging[it.contract.fullName()] = it.channel
        }

        val implementations = mutableMapOf<String, ClassInfo>()
        settings.implementations.forEach {
            implementations[it.contract.fullName()] = it.implementation
        }

        settings.contracts.forEach {
            if (it.collectedBy != ContractCollector.COLLECTED_BY_KAPT) {
                return@forEach
            }

            if (implementations.containsKey(it.name)) {
                if (messaging.containsKey(it.name)) {
                    generateMessageTranslator(processingEnv, it, implementations[it.name]!!)
                }
                return@forEach
            }

            val properties = reader.findPropertiesOfContract(it.name)
            if (null !== properties) {
                val implFile = ContractImplementationMainGenerator.generate(it, properties)
                ProcessorOutput.writeGeneratedFile(processingEnv, implFile)

                factoryMainGenerator.add(it.contract, implFile.target)
                if (messaging.containsKey(it.name)) {
                    generateMessageTranslator(processingEnv, it, implFile.target)
                }
            }
        }
        ProcessorOutput.writeGeneratedFile(processingEnv, factoryMainGenerator.generate(settings, global.packageName))
    }

    private fun generateMessageTranslator(
        processingEnv: ProcessingEnvironment,
        contractSetting: ContractSetting,
        implementation: ClassInfo
    ) {
        ProcessorOutput.writeGeneratedFile(
            processingEnv,
            MessageTranslatorMainGenerator.generate(contractSetting, implementation)
        )
        registerMessageTranslator(contractSetting.contract, implementation)
    }

    private fun generateLocalBuses(
        processingEnv: ProcessingEnvironment,
        processorSetting: ProcessorSetting,
        settings: GeneratorSettings,
        global: ClassInfo
    ) {
        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            LocalEventBusMainGenerator().generate(settings.eventHandlers, global.packageName),
            processorSetting.isDev
        )

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            LocalServiceBusMainGenerator().generate(settings.requestHandlers, global.packageName),
            processorSetting.isDev
        )

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            LocalCommandBusMainGenerator().generate(settings.commandHandlers, global.packageName),
            processorSetting.isDev
        )

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            LocalQueryBusMainGenerator().generate(settings.queryHandlers, global.packageName),
            processorSetting.isDev
        )

//        ProcessorOutput.writeGlobalFile(
//            processingEnv,
//            settings,
//            InfrastructureProviderMainGenerator().generate(settings, global.packageName)
//        )
    }
}