package net.ntworld.foundation.processor

import kotlinx.metadata.KmClass
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.ContractProperty
import net.ntworld.foundation.generator.type.KotlinMetadata
import org.jetbrains.annotations.NotNull
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

object ContractCollector {
    private val collected = mutableMapOf<String, ContractSetting>()

    // -----------------------------------------------------------------------------------------------------------------

    private fun shouldCollect(element: Element): Boolean {
        if (element !is TypeElement) {
            return false
        }

        if (!element.kind.isInterface) {
            return false
        }

        return !collected.contains(element.qualifiedName.toString())
    }

    private fun collectContractData(
        processingEnv: ProcessingEnvironment,
        element: TypeElement
    ): ContractSetting? {
        val metadataAnnotation = element.getAnnotation(Metadata::class.java)
        if (null === metadataAnnotation) {
            return null
        }


        val header = KotlinClassHeader(
            data1 = metadataAnnotation.data1,
            data2 = metadataAnnotation.data2,
            bytecodeVersion = metadataAnnotation.bytecodeVersion,
            extraInt = metadataAnnotation.extraInt,
            extraString = metadataAnnotation.extraString,
            kind = metadataAnnotation.kind,
            metadataVersion = metadataAnnotation.metadataVersion,
            packageName = metadataAnnotation.packageName
        )

        val metadata = KotlinClassMetadata.read(header) as? KotlinClassMetadata.Class ?: return null

        val kmClass = metadata.toKmClass()
        val propertyList = getPropertiesList(kmClass)

        val properties = mutableMapOf<String, ContractProperty>()
        for (property in propertyList) {
            val getterName = "get${property.capitalize()}"
            for (i in 0..element.enclosedElements.lastIndex) {
                val enclosedElement = element.enclosedElements[i]
                if (enclosedElement.kind == ElementKind.METHOD && enclosedElement.simpleName.toString() == getterName) {
                    properties[property] = buildContractProperty(property, enclosedElement, i)
                }
            }
        }

        return buildSetting(
            element,
            processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString(),
            element.simpleName.toString(),
            properties,
            header
        )
    }

    private fun buildContractProperty(
        property: String,
        getterElement: Element,
        index: Int
    ): ContractProperty {
        val annotations = getterElement.annotationMirrors
        val unknownAnnotations = mutableListOf<String>()
        var hasNotNullAnnotation = false
        annotations.forEach {
            val annotationElement = it.annotationType.asElement() as? TypeElement ?: return@forEach

            if (annotationElement.qualifiedName.toString() == NotNull::class.java.canonicalName) {
                hasNotNullAnnotation = true
                return@forEach
            }
        }

        return ContractProperty(
            name = property,
            order = index,
            unknownAnnotations = unknownAnnotations,
            hasNotNullAnnotation = hasNotNullAnnotation,
            hasFakedAnnotation = false,
            fakedType = null
        )
    }

    private fun getPropertiesList(kmClass: KmClass): List<String> {
        return kmClass.properties.map {
            it.name
        }
    }

    private fun buildSetting(
        element: TypeElement,
        packageName: String,
        className: String,
        properties: Map<String, ContractProperty>,
        header: KotlinClassHeader
    ): ContractSetting {
        val result = ContractSetting(
            contract = ClassInfo(packageName = packageName, className = className),
            metadata = KotlinMetadata(
                kind = header.kind,
                packageName = header.packageName,
                metadataVersion = header.metadataVersion.toSet(),
                bytecodeVersion = header.bytecodeVersion.toSet(),
                data1 = header.data1.toSet(),
                data2 = header.data2.toSet(),
                extraString = header.extraString,
                extraInt = header.extraInt
            ),
            supertypes = listOf(),
            properties = properties
        )
        collected[element.qualifiedName.toString()] = result
        return result
    }

    // -----------------------------------------------------------------------------------------------------------------

    fun reset() {
        collected.clear()
    }

    fun getCollectedSettings(): List<ContractSetting> {
        return collected.values.toList()
    }

    fun collect(processingEnv: ProcessingEnvironment, canonicalName: String) =
        collect(processingEnv, processingEnv.elementUtils.getTypeElement(canonicalName))

    fun collect(processingEnv: ProcessingEnvironment, element: Element): ContractSetting? {
        if (!shouldCollect(element)) {
            return null
        }
        return collectContractData(processingEnv, element as TypeElement)
    }
}