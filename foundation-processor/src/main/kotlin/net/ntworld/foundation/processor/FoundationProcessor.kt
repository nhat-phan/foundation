package net.ntworld.foundation.processor

import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.main.*
import net.ntworld.foundation.generator.test.ContractImplementationFactoryTestGenerator
import net.ntworld.foundation.generator.type.AnnotationProcessorRunInfo
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.processor.internal.*
import net.ntworld.foundation.processor.internal.AggregateFactoryProcessor
import net.ntworld.foundation.processor.internal.CommandHandlerProcessor
import net.ntworld.foundation.processor.internal.EventHandlerProcessor
import net.ntworld.foundation.processor.internal.Processor
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.util.FrameworkProcessor
import net.ntworld.foundation.processor.util.ProcessorOutput
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.lang.model.element.TypeElement
import kotlin.contracts.contract

@SupportedAnnotationTypes(
    FrameworkProcessor.Faked,
    FrameworkProcessor.Implementation,
    FrameworkProcessor.Handler,
    FrameworkProcessor.EventSourced,
    FrameworkProcessor.EventSourcing,
    FrameworkProcessor.EventSourcingMetadata,
    FrameworkProcessor.EventSourcingEncrypted
)
@SupportedOptions(FrameworkProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class FoundationProcessor : AbstractProcessor() {
    private val annotationList = listOf(
        FrameworkProcessor.Implementation,
        FrameworkProcessor.Handler,
        FrameworkProcessor.EventSourced,
        FrameworkProcessor.EventSourcing,
        FrameworkProcessor.EventSourcingMetadata,
        FrameworkProcessor.EventSourcingEncrypted
    )

    private val processors: List<Processor> = listOf(
        ImplementationProcessor(),
        FakedAnnotationProcessor(),
        EventHandlerProcessor(),
        CommandHandlerProcessor(),
        QueryHandlerProcessor(),
        EventSourcingProcessor(),
        AggregateFactoryProcessor()
    )

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        ContractCollector.reset()

        val start = System.currentTimeMillis()
        val processingAnnotations = mutableListOf<String>()
        if (null === annotations || null === roundEnv || !shouldProcess(annotations)) {
            return true
        }

        val currentSettings = ProcessorOutput.readSettingsFile(processingEnv)
        val lastRunInfo = currentSettings.annotationProcessorRunInfo.toMutableList()
        annotations.forEach {
            processingAnnotations.add(it.toString())
        }

        val processedSettings = processors.fold(currentSettings) { input, processor ->
            this.runProcessor(roundEnv, input, processor)
        }

        val mutableSettings = processedSettings.toMutable()
        ContractCollector.getCollectedSettings().forEach {
            mutableSettings.put(it)
        }

        val settings = mutableSettings.toGeneratorSettings()
        generate(settings)

        val end = System.currentTimeMillis()
        lastRunInfo.add(
            AnnotationProcessorRunInfo(
                annotations = processingAnnotations,
                startedAt = start,
                finishedAt = end,
                duration = (end - start)
            )
        )
        ProcessorOutput.updateSettingsFile(processingEnv, settings.copy(annotationProcessorRunInfo = lastRunInfo))

        return true
    }

    private fun shouldProcess(annotations: MutableSet<out TypeElement>): Boolean {
        annotations.forEach {
            if (annotationList.contains(it.qualifiedName.toString())) {
                return@shouldProcess true
            }
        }
        return false
    }

    private fun generate(settings: GeneratorSettings) {
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
        generateProviderAndBuses(settings, globalTarget)
    }

    private fun generateUnimplementedContracts(settings: GeneratorSettings, global: ClassInfo) {
        val reader = ContractReader(
            contractSettings = settings.contracts,
            fakedAnnotationSettings = settings.fakedAnnotations
        )

        // val factoryMainGenerator = ContractFactoryMainGenerator()
        val implementations = mutableMapOf<String, String>()
        settings.implementations.forEach {
            implementations[it.contract.fullName()] = it.name
        }

        settings.contracts.forEach {
            if (it.collectedBy !== ContractCollector.COLLECTED_BY_KAPT || implementations.containsKey(it.name)) {
                return@forEach
            }

            val properties = reader.findPropertiesOfContract(it.name)
            if (null !== properties) {
                val implFile = ContractImplementationMainGenerator.generate(it, properties)
                ProcessorOutput.writeGeneratedFile(processingEnv, implFile)

                /*
                val implFactoryFile = ContractImplementationFactoryMainGenerator.generate(
                    it, properties, implFile.target
                )
                ProcessorOutput.writeGeneratedFile(processingEnv, implFactoryFile)
                factoryMainGenerator.add(it.contract, implFactoryFile.target)
                */
                val implFactoryTestFile = ContractImplementationFactoryTestGenerator.generate(
                    it, properties, implFile.target
                )
                ProcessorOutput.writeGeneratedFile(processingEnv, implFactoryTestFile)
            }
        }
        // ProcessorOutput.writeGeneratedFile(processingEnv, factoryMainGenerator.generate(global.packageName))
    }

    private fun generateProviderAndBuses(settings: GeneratorSettings, global: ClassInfo) {
        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            LocalEventBusMainGenerator().generate(settings.eventHandlers, global.packageName)
        )

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            LocalCommandBusMainGenerator().generate(settings.commandHandlers, global.packageName)
        )

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            LocalQueryBusMainGenerator().generate(settings.queryHandlers, global.packageName)
        )

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            InfrastructureProviderMainGenerator().generate(settings, global.packageName)
        )
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
}