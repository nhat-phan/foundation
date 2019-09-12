package net.ntworld.foundation.processor

import net.ntworld.foundation.Use
import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.type.AnnotationProcessorRunInfo
import net.ntworld.foundation.processor.internal.generator.CodeGenerator
import net.ntworld.foundation.processor.internal.generator.ContractOnlyCodeGenerator
import net.ntworld.foundation.processor.internal.generator.DefaultCodeGenerator
import net.ntworld.foundation.processor.internal.processor.*
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.util.FrameworkProcessor
import net.ntworld.foundation.processor.util.ProcessorOutput
import net.ntworld.foundation.processor.util.ProcessorSetting
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

class FoundationProcessor : AbstractProcessor() {
    private val generators: List<CodeGenerator> = listOf(
        ContractOnlyCodeGenerator(),
        DefaultCodeGenerator()
    )
    private val processors: List<Processor> = listOf(
        ImplementationProcessor(),
        FakedAnnotationProcessor(),
        MessagingProcessor(),
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
        val currentSettings = getCurrentSettings(roundEnv)
        val lastRunInfo = currentSettings.annotationProcessorRunInfo.toMutableList()

        ContractCollector.reset()
        val settings = collectSettingsByProcessors(processorSetting, currentSettings, roundEnv)
        generate(processorSetting, settings)

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

    private fun getCurrentSettings(roundEnv: RoundEnvironment): GeneratorSettings {
        val settings = ProcessorOutput.readSettingsFile(processingEnv).toMutable()
        val annotatedByUseElements = roundEnv.getElementsAnnotatedWith(Use::class.java)
        annotatedByUseElements.forEach { element ->
            val value = element.getAnnotation(Use::class.java).settings
            val parsed = GeneratorSettings.fromBase64String(value)
            settings.merge(parsed)
        }
        return settings.toGeneratorSettings()
    }

    private fun collectSettingsByProcessors(
        processorSetting: ProcessorSetting,
        currentSettings: GeneratorSettings,
        roundEnv: RoundEnvironment
    ): GeneratorSettings {
        ContractCollector.reset()
        val processedSettings = processors.fold(currentSettings) { input, processor ->
            this.runProcessor(roundEnv, input, processor)
        }
        return ContractCollector.toGeneratorSettings(processedSettings, processorSetting.mode)
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

    private fun generate(processorSetting: ProcessorSetting, settings: GeneratorSettings) {
        this.generators.forEach {
            if (it.mode == processorSetting.mode) {
                it.generate(processingEnv, processorSetting, settings)
                return@generate
            }
        }
    }
}