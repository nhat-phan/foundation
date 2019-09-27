package net.ntworld.foundation.processor.internal.processor

import net.ntworld.foundation.Use
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.util.FrameworkProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element

internal class UseProcessor : Processor {
    override val annotations: List<Class<out Annotation>> = listOf(
        Use::class.java
    )

    override fun startProcess(settings: GeneratorSettings) {}

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        return settings
    }

    override fun shouldProcess(
        annotation: Class<out Annotation>,
        element: Element,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ): Boolean {
        return when (annotation) {
            Use::class.java -> true
            else -> false
        }
    }

    override fun process(
        annotation: Class<out Annotation>,
        elements: List<Element>,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ) = elements.forEach {
        val mirrors = it.annotationMirrors
        var contractTypeName = ""
        mirrors.forEach {
            if (it.annotationType.toString() == FrameworkProcessor.Use) {
                it.elementValues.forEach {
                    if (it.key.simpleName.toString() == "contract" &&
                        it.value.value.toString() != java.lang.Object::class.java.canonicalName
                    ) {
                        contractTypeName = it.value.value.toString()
                    }
                }
            }
        }

        if (contractTypeName.isEmpty()) {
            return@forEach
        }
        val contractElement = processingEnv.elementUtils.getTypeElement(contractTypeName)
        ContractCollector.collect(processingEnv, contractElement)
    }

}