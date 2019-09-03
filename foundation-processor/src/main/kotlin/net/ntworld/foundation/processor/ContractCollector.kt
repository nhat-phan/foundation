package net.ntworld.foundation.processor

import kotlinx.metadata.KmClass
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.syntheticMethodForAnnotations
import net.ntworld.foundation.Event
import net.ntworld.foundation.Faked
import net.ntworld.foundation.State
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.ContractProperty
import net.ntworld.foundation.generator.type.KotlinMetadata
import org.jetbrains.annotations.NotNull
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

object ContractCollector {
    private val BASE_CONTRACTS = arrayOf(
        Event::class.java.canonicalName,
        Query::class.java.canonicalName,
        Command::class.java.canonicalName,
        State::class.java.canonicalName
    )
    private const val KIND_SYNTHETIC_CLASS = 3
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
        val supertypes = this.findSupertypes(element, kmClass)
        val properties = this.findProperties(element, kmClass)

        return buildSetting(
            processingEnv,
            element,
            processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString(),
            element.simpleName.toString(),
            supertypes,
            properties,
            header
        )
    }

    private fun findSupertypes(element: TypeElement, kmClass: KmClass): List<String> {
        val result = mutableListOf<String>()
        for (supertype in kmClass.supertypes) {
            if (supertype.classifier !is KmClassifier.Class) {
                continue
            }
            val classifier = supertype.classifier as KmClassifier.Class
            val qualifiedName = classifier.name.replace('/', '.')
            if (!BASE_CONTRACTS.contains(qualifiedName)) {
                result.add(qualifiedName)
            }
        }
        return result
    }

    private fun findProperties(element: TypeElement, kmClass: KmClass): Map<String, ContractProperty> {
        val properties = mutableMapOf<String, ContractProperty>()
        val syntheticClassTypeElement = findSyntheticClassTypeElement(element)
        for (property in kmClass.properties) {
            val getterSignature = property.getterSignature
            if (null === getterSignature) {
                continue
            }

            val getterName = getterSignature.name
            for (i in 0..element.enclosedElements.lastIndex) {
                val enclosedElement = element.enclosedElements[i]
                if (enclosedElement.kind == ElementKind.METHOD && enclosedElement.simpleName.toString() == getterName) {
                    var syntheticMethodForAnnotationsName: String? = null
                    val syntheticMethodForAnnotations = property.syntheticMethodForAnnotations
                    if (null !== syntheticMethodForAnnotations) {
                        syntheticMethodForAnnotationsName = syntheticMethodForAnnotations.name
                    }

                    properties[property.name] = buildContractProperty(
                        property.name,
                        enclosedElement,
                        syntheticClassTypeElement,
                        syntheticMethodForAnnotationsName,
                        i
                    )
                }
            }
        }
        return properties
    }

    private fun findSyntheticClassTypeElement(element: TypeElement): TypeElement? {
        for (enclosedElement in element.enclosedElements) {
            if (enclosedElement.kind != ElementKind.CLASS) {
                continue
            }
            val metadataAnnotation = enclosedElement.getAnnotation(Metadata::class.java)
            if (null === metadataAnnotation) {
                return null
            }
            if (metadataAnnotation.kind != KIND_SYNTHETIC_CLASS) {
                return null
            }
            return enclosedElement as TypeElement
        }
        return null
    }

    private fun buildContractProperty(
        propertyName: String,
        getterElement: Element,
        syntheticClassTypeElement: TypeElement?,
        syntheticMethodForAnnotationsName: String?,
        index: Int
    ): ContractProperty {
        val getter = findContractPropertyOfElement(getterElement)
        val result = ContractProperty(
            name = propertyName,
            order = index,
            unknownAnnotations = getter.unknownAnnotations,
            hasNotNullAnnotation = getter.hasNotNullAnnotation,
            hasFakedAnnotation = getter.hasFakedAnnotation,
            fakedType = getter.fakedType
        )

        if (null === syntheticClassTypeElement || null === syntheticMethodForAnnotationsName) {
            return result
        }

        val element = syntheticClassTypeElement.enclosedElements.find {
            it.simpleName.toString() == syntheticMethodForAnnotationsName
        }
        if (null === element) {
            return result
        }

        val synthetic = findContractPropertyOfElement(element)

        var fakeType: String? = null
        if (getter.hasFakedAnnotation) fakeType = getter.fakedType
        if (synthetic.hasFakedAnnotation) fakeType = synthetic.fakedType

        return ContractProperty(
            name = propertyName,
            order = index,
            unknownAnnotations = (getter.unknownAnnotations + synthetic.unknownAnnotations).distinct(),
            hasNotNullAnnotation = getter.hasNotNullAnnotation || synthetic.hasNotNullAnnotation,
            hasFakedAnnotation = getter.hasFakedAnnotation || synthetic.hasFakedAnnotation,
            fakedType = fakeType
        )
    }

    private fun findContractPropertyOfElement(element: Element): ContractProperty {
        val annotations = element.annotationMirrors
        val unknownAnnotations = mutableListOf<String>()
        var hasFakedAnnotation = false
        var fakedType: String? = null
        var hasNotNullAnnotation = false
        annotations.forEach {
            val annotationElement = it.annotationType.asElement() as? TypeElement ?: return@forEach
            val qualifiedName = annotationElement.qualifiedName.toString()

            if (qualifiedName == NotNull::class.java.canonicalName) {
                hasNotNullAnnotation = true
                return@forEach
            }

            if (qualifiedName == Faked::class.java.canonicalName) {
                hasFakedAnnotation = true
                fakedType = element.getAnnotation(Faked::class.java).type
                return@forEach
            }

            unknownAnnotations.add(qualifiedName)
        }
        return ContractProperty(
            name = "",
            order = 0,
            unknownAnnotations = unknownAnnotations,
            hasNotNullAnnotation = hasNotNullAnnotation,
            hasFakedAnnotation = hasFakedAnnotation,
            fakedType = fakedType
        )
    }

    private fun buildSetting(
        processingEnv: ProcessingEnvironment,
        element: TypeElement,
        packageName: String,
        className: String,
        supertypes: List<String>,
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
            supertypes = supertypes,
            properties = sortProperties(properties)
        )
        supertypes.forEach {
            collect(processingEnv, it)
        }
        collected[element.qualifiedName.toString()] = result
        return result
    }

    private fun sortProperties(properties: Map<String, ContractProperty>): Map<String, ContractProperty> {
        val keys = properties.keys.toList()
        val order = properties.mapValues { it.value.order }
        val sortedKeys = keys.sortedWith(Comparator { o1, o2 -> order[o1]!!.compareTo(order[o2]!!) })
        val result = mutableMapOf<String, ContractProperty>()
        for (key in sortedKeys) {
            result[key] = properties[key]!!
        }
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