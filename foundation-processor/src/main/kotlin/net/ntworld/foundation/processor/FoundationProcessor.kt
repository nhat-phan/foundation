package net.ntworld.foundation.processor

import net.ntworld.foundation.generator.*
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
    private val processors: List<Processor> = listOf(
        EventHandlerProcessor()
        // EventHandlerProcessor(),
        // EventSourcingProcessor(),
        // AggregateFactoryProcessor()
    )

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        if (null === annotations || null === roundEnv) {
            return true
        }

        val settings = processors.fold(ProcessorOutput.readSettingsFile(processingEnv)) { input, processor ->
            this.runProcessor(roundEnv, input, processor)
        }

        val final = settings.copy(
            description = settings.description + " x "
        )

        ProcessorOutput.updateSettingsFile(processingEnv, final)

        settings.events.forEach {
            ProcessorOutput.writeGeneratedFile(processingEnv, EventEntityGenerator.generate(it))
            ProcessorOutput.writeGeneratedFile(processingEnv, EventConverterGenerator.generate(it))
            ProcessorOutput.writeGeneratedFile(processingEnv, EventMessageTranslatorGenerator.generate(it))
        }

        settings.aggregateFactories.forEach {
            ProcessorOutput.writeGeneratedFile(processingEnv, AggregateFactoryGenerator.generate(it))
        }
        ProcessorOutput.writeGeneratedFile(processingEnv, InfrastructureProviderGenerator().generate(settings))

        return true
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