package net.ntworld.foundation.processor.internal

import net.ntworld.foundation.generator.GeneratorSettings
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element

internal interface Processor {

    val annotations: List<Class<out Annotation>>

    fun startProcess(settings: GeneratorSettings)

    fun applySettings(settings: GeneratorSettings): GeneratorSettings

    fun shouldProcess(
        annotation: Class<out Annotation>,
        element: Element,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ): Boolean

    fun process(
        annotation: Class<out Annotation>,
        elements: List<Element>,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    )
}