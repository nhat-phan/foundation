package net.ntworld.foundation.processor

import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.type.AnnotationProcessorRunInfo
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes(
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
        EventHandlerProcessor(),
        CommandHandlerProcessor(),
        QueryHandlerProcessor(),
        EventSourcingProcessor(),
        AggregateFactoryProcessor()
    )

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
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

        val settings = processors.fold(currentSettings) { input, processor ->
            this.runProcessor(roundEnv, input, processor)
        }

        val end = System.currentTimeMillis()
        lastRunInfo.add(
            AnnotationProcessorRunInfo(
                annotations = processingAnnotations,
                startedAt = start,
                finishedAt = end,
                duration = (end - start)
            )
        )
        val final = settings.copy(
            annotationProcessorRunInfo = lastRunInfo
        )
        ProcessorOutput.updateSettingsFile(processingEnv, final)
        generate(final)

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
        settings.events.forEach {
            ProcessorOutput.writeGeneratedFile(processingEnv, EventEntityGenerator.generate(it))
            ProcessorOutput.writeGeneratedFile(processingEnv, EventConverterGenerator.generate(it))
            ProcessorOutput.writeGeneratedFile(processingEnv, EventMessageTranslatorGenerator.generate(it))
        }

        settings.aggregateFactories.forEach {
            ProcessorOutput.writeGeneratedFile(processingEnv, AggregateFactoryGenerator.generate(it))
        }

        val globalTarget = InfrastructureProviderGenerator().findTarget(settings)

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            LocalEventBusGenerator().generate(settings.eventHandlers, globalTarget.packageName)
        )

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            LocalCommandBusGenerator().generate(settings.commandHandlers, globalTarget.packageName)
        )

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            LocalQueryBusGenerator().generate(settings.queryHandlers, globalTarget.packageName)
        )

        ProcessorOutput.writeGlobalFile(
            processingEnv,
            settings,
            InfrastructureProviderGenerator().generate(settings, globalTarget.packageName)
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