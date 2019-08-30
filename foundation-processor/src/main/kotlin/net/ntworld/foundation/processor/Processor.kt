package net.ntworld.foundation.processor

import net.ntworld.foundation.generator.GeneratorSettings
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element

interface Processor {

    val annotations: List<Class<out Annotation>>

    fun shouldProcess(annotation: Class<out Annotation>, element: Element): Boolean

    fun startProcess(settings: GeneratorSettings)

    fun process(
        annotation: Class<out Annotation>,
        elements: List<Element>,
        processingEnv: ProcessingEnvironment,
        roundedEnv: RoundEnvironment
    )

    fun toGeneratorSettings(): GeneratorSettings
}