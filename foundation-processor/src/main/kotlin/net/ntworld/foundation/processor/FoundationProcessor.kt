package net.ntworld.foundation.processor

import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.main.*
import net.ntworld.foundation.generator.type.AnnotationProcessorRunInfo
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.util.ContractReader
import net.ntworld.foundation.processor.internal.*
import net.ntworld.foundation.processor.internal.AggregateFactoryProcessor
import net.ntworld.foundation.processor.internal.CommandHandlerProcessor
import net.ntworld.foundation.processor.internal.EventHandlerProcessor
import net.ntworld.foundation.processor.internal.Processor
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.util.FrameworkProcessor
import net.ntworld.foundation.processor.util.ProcessorOutput
import net.ntworld.foundation.processor.util.ProcessorSetting
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

class FoundationProcessor : AbstractProcessor() {
    private val processors: List<Processor> = listOf(
        ImplementationProcessor(),
        FakedAnnotationProcessor(),
        EventHandlerProcessor(),
        CommandHandlerProcessor(),
        QueryHandlerProcessor(),
        RequestHandlerProcessor(),
        EventSourcingProcessor(),
        AggregateFactoryProcessor(),
        FakedPropertyProcessor()
    )

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return FrameworkProcessor.SUPPORTED_ANNOTATION_TYPES.toMutableSet()
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return FrameworkProcessor.SUPPORTED_OPTIONS.toMutableSet()
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        val start = System.currentTimeMillis()
        if (null === annotations || null === roundEnv || !shouldProcess(annotations)) {
            return true
        }

        val processorSetting = ProcessorSetting.read(processingEnv)
        val currentSettings = ProcessorOutput.readSettingsFile(processingEnv)
        val lastRunInfo = currentSettings.annotationProcessorRunInfo.toMutableList()

        val settings = collectSettingsByProcessors(currentSettings, roundEnv)
        when (processorSetting.mode) {
            ProcessorSetting.Mode.Default -> generateModeDefault(processorSetting, settings)
            ProcessorSetting.Mode.ContractOnly -> generateModeContractOnly(processorSetting, settings)
        }

        val end = System.currentTimeMillis()
        lastRunInfo.add(
            AnnotationProcessorRunInfo(
                annotations = annotations.map { it.toString() },
                startedAt = start,
                finishedAt = end,
                duration = (end - start)
            )
        )
        ProcessorOutput.updateSettingsFile(
            processingEnv,
            if (processorSetting.isDev) settings.copy(annotationProcessorRunInfo = lastRunInfo) else settings,
            processorSetting.isDev
        )

        return true
    }

    private fun shouldProcess(annotations: MutableSet<out TypeElement>): Boolean {
        annotations.forEach {
            if (FrameworkProcessor.SUPPORTED_ANNOTATION_TYPES.contains(it.qualifiedName.toString())) {
                return@shouldProcess true
            }
        }
        return false
    }

    private fun collectSettingsByProcessors(
        currentSettings: GeneratorSettings,
        roundEnv: RoundEnvironment
    ): GeneratorSettings {
        ContractCollector.reset()
        val processedSettings = processors.fold(currentSettings) { input, processor ->
            this.runProcessor(roundEnv, input, processor)
        }
        val mutableSettings = processedSettings.toMutable()
        ContractCollector.getCollectedSettings().forEach {
            mutableSettings.put(it)
        }

        return mutableSettings.toGeneratorSettings()
    }

    private fun runProcessor(
        roundEnv: RoundEnvironment,
        settings: GeneratorSettings,
        processor: Processor
    ): GeneratorSettings {
        processor.startProcess(settings)
        processor.annotations.forEach { annotation ->
            val annotatedElements = roundEnv.getElementsAnnotatedWith(annotation)
            val elements = annotatedElements.filter {
                processor.shouldProcess(annotation, it, processingEnv, roundEnv)
            }
            processor.process(annotation, elements, processingEnv, roundEnv)
        }
        return processor.applySettings(settings)
    }

    private fun generateModeContractOnly(processorSetting: ProcessorSetting, settings: GeneratorSettings) {

    }

    private fun generateModeDefault(processorSetting: ProcessorSetting, settings: GeneratorSettings) {
        settings.eventSourcings.forEach {
            ProcessorOutput.writeGeneratedFile(processingEnv, EventEntityMainGenerator.generate(it))
            ProcessorOutput.writeGeneratedFile(processingEnv, EventConverterMainGenerator.generate(it))
            ProcessorOutput.writeGeneratedFile(processingEnv, EventMessageTranslatorMainGenerator.generate(it))
        }

        settings.aggregateFactories.forEach {
            ProcessorOutput.writeGeneratedFile(processingEnv, AggregateFactoryMainGenerator.generate(it))
        }

        val globalTarget = InfrastructureProviderMainGenerator().findTarget(settings)
        generateUnimplementedContracts(settings, globalTarget)
        generateProviderAndBuses(processorSetting, settings, globalTarget)
    }

    private fun generateUnimplementedContracts(settings: GeneratorSettings, global: ClassInfo) {
        val reader = ContractReader(
            contractSettings = settings.contracts,
            fakedAnnotationSettings = settings.fakedAnnotations
        )

        val factoryMainGenerator = ContractFactoryMainGenerator()
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
                val implFile = ContractImplementationMainGenerator.generate(it, properties)
                ProcessorOutput.writeGeneratedFile(processingEnv, implFile)

                factoryMainGenerator.add(it.contract, implFile.target)
            }
        }
        ProcessorOutput.writeGeneratedFile(processingEnv, factoryMainGenerator.generate(settings, global.packageName))
    }

    private fun generateProviderAndBuses(
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