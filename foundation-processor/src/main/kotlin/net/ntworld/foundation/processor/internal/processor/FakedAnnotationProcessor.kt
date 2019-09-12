package net.ntworld.foundation.processor.internal.processor

import net.ntworld.foundation.Faked
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.FakedAnnotationSetting
import net.ntworld.foundation.generator.type.ClassInfo
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

internal class FakedAnnotationProcessor : Processor {
    override val annotations: List<Class<out Annotation>> = listOf(
        Faked::class.java
    )

    internal data class CollectedFakedAnnotation(
        val packageName: String,
        val className: String,
        val fakedType: String
    )

    private val data = mutableMapOf<String, CollectedFakedAnnotation>()

    override fun startProcess(settings: GeneratorSettings) {
        data.clear()
        settings.fakedAnnotations.forEach { item ->
            data[item.name] =
                CollectedFakedAnnotation(
                    packageName = item.annotation.packageName,
                    className = item.annotation.className,
                    fakedType = item.fakedType
                )
        }
    }

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        val fakedAnnotations = data.values
            .filter {
                it.packageName.isNotEmpty() && it.className.isNotEmpty() && it.fakedType.isNotEmpty()
            }
            .map {
                FakedAnnotationSetting(
                    annotation = ClassInfo(
                        packageName = it.packageName,
                        className = it.className
                    ),
                    fakedType = it.fakedType
                )
            }
        return settings.copy(fakedAnnotations = fakedAnnotations)
    }

    override fun shouldProcess(
        annotation: Class<out Annotation>,
        element: Element,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ): Boolean {
        return when (annotation) {
            Faked::class.java -> {
                element.kind == ElementKind.ANNOTATION_TYPE
            }

            else -> false
        }
    }

    override fun process(
        annotation: Class<out Annotation>,
        elements: List<Element>,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ) = elements.forEach {
        val packageElement = processingEnv.elementUtils.getPackageOf(it)
        val className = it.simpleName.toString()
        val key = "${packageElement.qualifiedName}.$className"

        val fakedAnnotation = it.getAnnotation(Faked::class.java)
        if (null === fakedAnnotation) {
            return@forEach
        }

        data[key] = CollectedFakedAnnotation(
            packageName = packageElement.qualifiedName.toString(),
            className = className,
            fakedType = fakedAnnotation.type
        )
    }
}