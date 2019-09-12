package net.ntworld.foundation.processor.internal.processor

import kotlinx.metadata.jvm.KotlinClassMetadata
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.syntheticMethodForAnnotations
import net.ntworld.foundation.Faked
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.FakedPropertySetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.util.KotlinMetadataUtil
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

class FakedPropertyProcessor : Processor {
    override val annotations: List<Class<out Annotation>> = listOf(
        Faked::class.java
    )

    internal data class CollectedFakedProperty(
        val packageName: String,
        val className: String,
        val property: String,
        val fakedType: String
    )

    private val data = mutableMapOf<String, CollectedFakedProperty>()

    override fun startProcess(settings: GeneratorSettings) {
        data.clear()
        settings.fakedProperties.forEach { item ->
            data[item.name] = CollectedFakedProperty(
                packageName = item.contract.packageName,
                className = item.contract.className,
                property = item.property,
                fakedType = item.fakedType
            )
        }
    }

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        val fakedProperties = data
            .filter {
                it.value.packageName.isNotEmpty() &&
                    it.value.className.isNotEmpty() &&
                    it.value.property.isNotEmpty() &&
                    it.value.fakedType.isNotEmpty()
            }
            .map {
                FakedPropertySetting(
                    contract = ClassInfo(
                        className = it.value.className,
                        packageName = it.value.packageName
                    ),
                    property = it.value.property,
                    fakedType = it.value.fakedType
                )
            }
        return settings.copy(fakedProperties = fakedProperties)
    }

    override fun shouldProcess(
        annotation: Class<out Annotation>,
        element: Element,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ): Boolean {
        return when (annotation) {
            Faked::class.java -> {
                element.kind == ElementKind.METHOD || element.kind == ElementKind.FIELD
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
        val fakedAnnotation = it.getAnnotation(Faked::class.java)
        if (null === fakedAnnotation) {
            return@forEach
        }

        if (it.enclosingElement.kind == ElementKind.INTERFACE && it.enclosingElement is TypeElement) {
            processContract(processingEnv, it, it.enclosingElement as TypeElement, fakedAnnotation.type)
            return@forEach
        }

        val header = KotlinMetadataUtil.getKotlinClassHeaderFromElement(it.enclosingElement)
        if (null === header || header.kind != KIND_SYNTHETIC_CLASS) {
            return@forEach
        }

        val enclosingElement = it.enclosingElement.enclosingElement
        if (enclosingElement.kind == ElementKind.INTERFACE && enclosingElement is TypeElement) {
            processContract(processingEnv, it, enclosingElement, fakedAnnotation.type)
            ContractCollector.collect(processingEnv, enclosingElement)
            return@forEach
        }
    }

    private fun processContract(
        processingEnv: ProcessingEnvironment,
        element: Element,
        enclosing: TypeElement,
        fakedType: String
    ) {
        val header = KotlinMetadataUtil.getKotlinClassHeaderFromElement(enclosing)
        if (null === header || header.kind != KIND_CLASS) {
            return
        }
        val metadata = KotlinClassMetadata.read(header) as? KotlinClassMetadata.Class ?: return
        val kmClass = metadata.toKmClass()
        kmClass.properties.forEach { property ->
            val getterSignature = property.getterSignature
            if (null !== getterSignature) {
                val getter = getterSignature.name
                if (getter == element.simpleName.toString()) {
                    return collectData(processingEnv, enclosing, property.name, fakedType)
                }
            }

            val syntheticMethodForAnnotationsName = property.syntheticMethodForAnnotations
            if (null !== syntheticMethodForAnnotationsName) {
                val syntheticMethod = syntheticMethodForAnnotationsName.name
                if (syntheticMethod == element.simpleName.toString()) {
                    return collectData(processingEnv, enclosing, property.name, fakedType)
                }
            }
        }
    }

    private fun collectData(
        processingEnv: ProcessingEnvironment,
        enclosing: TypeElement,
        propertyName: String,
        fakedType: String
    ) {
        val packageElement = processingEnv.elementUtils.getPackageOf(enclosing)
        val className = enclosing.simpleName.toString()
        val key = "${packageElement.qualifiedName}.$className\$$propertyName"

        data[key] = CollectedFakedProperty(
            packageName = packageElement.qualifiedName.toString(),
            className = className,
            property = propertyName,
            fakedType = fakedType
        )
    }

    companion object {
        private const val KIND_CLASS = 1
        private const val KIND_SYNTHETIC_CLASS = 3
    }
}