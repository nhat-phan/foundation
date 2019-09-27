package net.ntworld.foundation.processor.internal.generator

import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.Platform
import net.ntworld.foundation.generator.main.*
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.setting.HandlerSetting
import net.ntworld.foundation.generator.setting.MessagingSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.ComponentType
import net.ntworld.foundation.generator.type.Property
import net.ntworld.foundation.generator.util.ContractReader
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.util.ProcessorOutput
import net.ntworld.foundation.processor.util.ProcessorSetting
import net.ntworld.foundation.processor.util.ProcessorUtil
import javax.annotation.processing.ProcessingEnvironment

internal class DefaultCodeGenerator : CodeGenerator {


    override val mode: ProcessorSetting.Mode = ProcessorSetting.Mode.Default

    private val infrastructureProviderMainGenerator = InfrastructureProviderMainGenerator()
    private val messageChannelDictionaryMainGenerator = MessageChannelDictionaryMainGenerator()

    override fun generate(
        processingEnv: ProcessingEnvironment,
        processorSetting: ProcessorSetting,
        generatorSettings: GeneratorSettings
    ) {
        val namespace = ProcessorUtil.findGlobalNamespace(processorSetting, generatorSettings)

        generateEventSourcing(processingEnv, generatorSettings)
        generateAggregateFactories(processingEnv, generatorSettings)
        generateUnimplementedContracts(processingEnv, generatorSettings, namespace)
        generateLocalBuses(processingEnv, processorSetting, generatorSettings, namespace)
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
        namespace: String
    ) {
        val reader = ContractReader(
            contractSettings = settings.contracts,
            fakedAnnotationSettings = settings.fakedAnnotations,
            fakedPropertySettings = settings.fakedProperties
        )

        val factoryMainGenerator = ContractFactoryMainGenerator()
        val messagings = mutableMapOf<String, MessagingSetting>()
        settings.messagings.forEach {
            messagings[it.contract.fullName()] = it
        }

        val implementations = mutableMapOf<String, ClassInfo>()
        settings.implementations.forEach {
            implementations[it.contract.fullName()] = it.implementation
        }

        settings.contracts.forEach {
            generateUnimplementedContract(processingEnv, reader, it, implementations, messagings, factoryMainGenerator)
        }
        ProcessorOutput.writeGeneratedFile(processingEnv, factoryMainGenerator.generate(settings, namespace))
    }

    private fun generateUnimplementedContract(
        processingEnv: ProcessingEnvironment,
        reader: ContractReader,
        contract: ContractSetting,
        implementations: Map<String, ClassInfo>,
        messagings: Map<String, MessagingSetting>,
        factoryMainGenerator: ContractFactoryMainGenerator
    ) {
        if (contract.collectedBy != ContractCollector.COLLECTED_BY_KAPT) {
            return
        }

        val properties = reader.findPropertiesOfContract(contract.name)
        if (null === properties) {
            return
        }

        val type = reader.findComponentType(contract.name)
        val implementation = generateImplementationForContractIfNeeded(
            processingEnv, properties, contract, implementations, factoryMainGenerator
        )
        if (messagings.containsKey(contract.name) || TYPES_HAVE_MESSAGE_TRANSLATOR.contains(type)) {
            generateMessageTranslator(
                processingEnv,
                contract,
                messagings[contract.name],
                implementation,
                properties
            )
        }
    }

    private fun generateImplementationForContractIfNeeded(
        processingEnv: ProcessingEnvironment,
        properties: Map<String, Property>,
        contract: ContractSetting,
        implementations: Map<String, ClassInfo>,
        factoryMainGenerator: ContractFactoryMainGenerator
    ): ClassInfo {
        val impl = implementations[contract.name]
        if (null === impl) {
            val implFile = ContractImplementationMainGenerator.generate(contract, properties)
            ProcessorOutput.writeGeneratedFile(processingEnv, implFile)

            factoryMainGenerator.add(contract.contract, implFile.target)
            return implFile.target
        }

        return impl
    }

    private fun generateMessageTranslator(
        processingEnv: ProcessingEnvironment,
        contractSetting: ContractSetting,
        messagingSetting: MessagingSetting?,
        implementation: ClassInfo,
        properties: Map<String, Property>
    ) {
        messageChannelDictionaryMainGenerator.add(contractSetting.contract)

        val translator = MessageTranslatorMainGenerator.generate(
            contractSetting,
            messagingSetting,
            implementation,
            properties
        )
        ProcessorOutput.writeGeneratedFile(processingEnv, translator)

        infrastructureProviderMainGenerator.registerMessageTranslator(contractSetting.contract, translator.target)
    }

    private fun generateLocalBuses(
        processingEnv: ProcessingEnvironment,
        processorSetting: ProcessorSetting,
        settings: GeneratorSettings,
        namespace: String
    ) {
        generateLocalBus(LocalEventBusMainGenerator(), processingEnv, processorSetting, settings, namespace) {
            settings.eventHandlers
        }

        generateLocalBus(LocalServiceBusMainGenerator(), processingEnv, processorSetting, settings, namespace) {
            settings.requestHandlers
        }

        generateLocalBus(LocalCommandBusMainGenerator(), processingEnv, processorSetting, settings, namespace) {
            settings.commandHandlers
        }

        generateLocalBus(LocalQueryBusMainGenerator(), processingEnv, processorSetting, settings, namespace) {
            settings.queryHandlers
        }

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            messageChannelDictionaryMainGenerator.generate(settings.messagings, namespace),
            processorSetting.isDev
        )

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            infrastructureProviderMainGenerator.generate(Platform.Jvm, settings, namespace),
            processorSetting.isDev
        )
    }

    private fun <T : HandlerSetting> generateLocalBus(
        localBusGenerator: AbstractLocalBusMainGenerator<T>,
        processingEnv: ProcessingEnvironment,
        processorSetting: ProcessorSetting,
        settings: GeneratorSettings,
        namespace: String,
        handlerBlock: () -> List<T>
    ) {
        val localBus = localBusGenerator.generate(handlerBlock(), namespace)
        ProcessorOutput.writeGlobalFile(processingEnv, settings, localBus, processorSetting.isDev)
        infrastructureProviderMainGenerator.addToConstructorComposer(
            localBus.target.className,
            localBusGenerator.getConstructor()
        )
        infrastructureProviderMainGenerator.setLocalBusesStatus(
            localBus.target.className,
            !localBus.empty
        )
    }

    companion object {
        private val TYPES_HAVE_MESSAGE_TRANSLATOR = listOf<ComponentType>(
            ComponentType.Event,
            ComponentType.Command,
            ComponentType.Query,
            ComponentType.Request,
            ComponentType.Response
        )
    }
}