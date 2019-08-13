package net.ntworld.foundation.annotation.processor

import net.ntworld.foundation.annotation.generator.HelloGenerator
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.KClass

@SupportedAnnotationTypes("net.ntworld.foundation.annotation.EventType")
@SupportedOptions(EventTypeProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class EventTypeProcessor: AbstractProcessor() {
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        println("Hello from EventTypeProcessor")
        if (null === annotations || null === roundEnv) {
            return true
        }

        val type = annotations.find {
            it.qualifiedName.toString() == "net.ntworld.foundation.annotation.EventType"
        }
        if (null === type) {
            return true
        }

        val elements = roundEnv.getElementsAnnotatedWith(type)
        val logs = mutableListOf<String>()
        elements.forEach {
            if (it.kind.isClass) {
                logs += "class: ${it.simpleName}"
                logs += "package: " + (it.enclosingElement as PackageElement).qualifiedName
            }
            it.enclosedElements.forEach {
                findAnnotationOfElement(it, logs)
            }
        }

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Can't find the target directory for generated Kotlin files.")
            return false
        }

        HelloGenerator().generate().writeTo(File(kaptKotlinGeneratedDir))
        File(kaptKotlinGeneratedDir + "/log.txt").writeText(
            logs.joinToString("\n")
        )
        return true
    }

    private fun findAnnotationOfElement(element: Element, logs: MutableList<String>) {
        logs += "-----"
        logs += "simpleName: " + element.simpleName
        logs += "kind: " + element.kind.toString()

        element.annotationMirrors.forEach {
            logs += "=========="
            logs += "type ${it.annotationType}"
        }
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}