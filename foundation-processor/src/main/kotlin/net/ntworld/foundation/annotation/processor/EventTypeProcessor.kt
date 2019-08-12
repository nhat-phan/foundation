package net.ntworld.foundation.annotation.processor

import net.ntworld.foundation.annotation.generator.HelloGenerator
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes("net.ntworld.foundation.annotation.EventType")
@SupportedOptions(EventTypeProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class EventTypeProcessor: AbstractProcessor() {
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        println("Hello from EventTypeProcessor")

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }

        HelloGenerator().generate().writeTo(File(kaptKotlinGeneratedDir))

        return true
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}