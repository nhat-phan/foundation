package net.ntworld.foundation.processor.util

import kotlinx.metadata.KmClass
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import kotlinx.metadata.jvm.getterSignature
import kotlinx.metadata.jvm.syntheticMethodForAnnotations
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.MutableGeneratorSettings
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.setting.FakedAnnotationSetting
import net.ntworld.foundation.generator.setting.FakedPropertySetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.ContractProperty
import net.ntworld.foundation.generator.type.KotlinMetadata
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

internal object ContractCollector {
    private val BASE_CONTRACTS = arrayOf(
        FrameworkProcessor.Contract,
        "kotlin.Any"
    )
    private const val KIND_SYNTHETIC_CLASS = 3
    private val collected = mutableMapOf<String, ContractSetting>()
    private const val COLLECTED_BY_SUPERTYPE = "kapt-supertype"
    internal const val COLLECTED_BY_KAPT = "kapt"

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
        element: TypeElement,
        collectedBy: String
    ): ContractSetting? {
        val header = KotlinMetadataUtil.getKotlinClassHeaderFromElement(element)
        if (null === header) {
            return null
        }

        val metadata = KotlinClassMetadata.read(header) as? KotlinClassMetadata.Class ?: return null
        val kmClass = metadata.toKmClass()
        val supertypes = findSupertypes(processingEnv, kmClass)
        val properties = findProperties(processingEnv, element, kmClass)

        return buildSetting(
            processingEnv,
            element,
            processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString(),
            element.simpleName.toString(),
            supertypes,
            properties,
            header,
            collectedBy
        )
    }

    private fun findSupertypes(processingEnv: ProcessingEnvironment, kmClass: KmClass): List<String> {
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

            KotlinMetadataUtil.findPotentialContractsInTypeArguments(supertype.arguments).forEach {
                collect(processingEnv, it, COLLECTED_BY_KAPT)
            }
        }
        return result
    }

    private fun findProperties(
        processingEnv: ProcessingEnvironment,
        element: TypeElement,
        kmClass: KmClass
    ): Map<String, ContractProperty> {
        val properties = mutableMapOf<String, ContractProperty>()
        val syntheticClassTypeElement = findSyntheticClassTypeElement(element)
        for (property in kmClass.properties) {
            val getterSignature = property.getterSignature
            if (null === getterSignature) {
                continue
            }

            collectContractsInType(processingEnv, property.returnType)

            val offset = if (null === syntheticClassTypeElement) 1 else 0
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
                        i + offset
                    )
                }
            }
        }
        return properties
    }

    private fun collectContractsInType(processingEnv: ProcessingEnvironment, kmType: KmType) {
        KotlinMetadataUtil.findPotentialContractsInKmType(kmType).forEach {
            collect(processingEnv, it, COLLECTED_BY_KAPT)
        }
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
        val getter = ContractPropertyUtil.makeByElement(getterElement)
        val result = ContractProperty(
            name = propertyName,
            order = index,
            unknownAnnotations = getter.unknownAnnotations,
            hasNotNullAnnotation = getter.hasNotNullAnnotation,
            hasFakedAnnotation = getter.hasFakedAnnotation,
            hasNullableAnnotation = getter.hasNullableAnnotation,
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

        val synthetic = ContractPropertyUtil.makeByElement(element)

        var fakeType: String? = null
        if (getter.hasFakedAnnotation) fakeType = getter.fakedType
        if (synthetic.hasFakedAnnotation) fakeType = synthetic.fakedType

        return ContractProperty(
            name = propertyName,
            order = index,
            unknownAnnotations = (getter.unknownAnnotations + synthetic.unknownAnnotations).distinct(),
            hasNotNullAnnotation = getter.hasNotNullAnnotation || synthetic.hasNotNullAnnotation,
            hasNullableAnnotation = getter.hasNullableAnnotation || synthetic.hasNullableAnnotation,
            hasFakedAnnotation = getter.hasFakedAnnotation || synthetic.hasFakedAnnotation,
            fakedType = fakeType
        )
    }

    private fun buildSetting(
        processingEnv: ProcessingEnvironment,
        element: TypeElement,
        packageName: String,
        className: String,
        supertypes: List<String>,
        properties: Map<String, ContractProperty>,
        header: KotlinClassHeader,
        collectedBy: String
    ): ContractSetting {
        val result = ContractSetting(
            contract = ClassInfo(packageName = packageName, className = className),
            metadata = KotlinMetadata.fromKotlinClassHeader(header),
            supertypes = supertypes,
            properties = ContractPropertyUtil.sortProperties(properties),
            collectedBy = collectedBy
        )
        supertypes.forEach {
            collect(processingEnv, it)
        }
        collected[element.qualifiedName.toString()] = result
        return result
    }

    // -----------------------------------------------------------------------------------------------------------------

    fun reset(): ContractCollector {
        collected.clear()
        return this
    }

    private fun collect(
        processingEnv: ProcessingEnvironment,
        canonicalName: String,
        type: String = COLLECTED_BY_SUPERTYPE
    ) {
        val element = processingEnv.elementUtils.getTypeElement(canonicalName)
        if (null !== element) {
            collect(processingEnv, element, type)
        }
    }

    fun collect(
        processingEnv: ProcessingEnvironment,
        element: Element,
        collectedBy: String = COLLECTED_BY_KAPT
    ): ContractSetting? {
        if (!shouldCollect(element)) {
            return null
        }
        return collectContractData(
            processingEnv,
            element as TypeElement,
            collectedBy
        )
    }

    // -----------------------------------------------------------------------------------------------------------------

    fun toGeneratorSettings(settings: GeneratorSettings, mode: ProcessorSetting.Mode): GeneratorSettings {
        val mutableSettings = settings.toMutable()
        if (mode == ProcessorSetting.Mode.Default) {
            collected.values.forEach {
                mutableSettings.put(it)
            }
        } else {
            toGeneratorSettingsModeContractsOnly(mutableSettings)
        }

        return mutableSettings.toGeneratorSettings()
    }

    private fun toGeneratorSettingsModeContractsOnly(mutableSettings: MutableGeneratorSettings) {
        collected.values.forEach {
            copyContractPropertiesToFakedPropertiesIfNeeded(
                mutableSettings,
                it.contract,
                it.properties.values
            )
        }
    }

    private fun copyContractPropertiesToFakedPropertiesIfNeeded(
        mutableSettings: MutableGeneratorSettings,
        contract: ClassInfo,
        properties: Collection<ContractProperty>
    ) = properties.forEach { property ->
        val key = "${contract.fullName()}\$${property.name}"
        if (mutableSettings.hasFakedPropertySetting(key)) {
            return@forEach
        }

        if (null !== property.fakedType && property.fakedType!!.isEmpty()) {
            return@forEach
        }

        if (property.unknownAnnotations.isEmpty()) {
            return@forEach
        }

        // find & map unknown annotations
        val fakedType = findFakedTypeByUnknownAnnotations(mutableSettings, property.unknownAnnotations)
        if (fakedType.isEmpty()) {
            return@forEach
        }

        mutableSettings.put(
            FakedPropertySetting(
                contract = contract,
                property = property.name,
                fakedType = fakedType
            )
        )
    }

    private fun findFakedTypeByUnknownAnnotations(
        mutableSettings: MutableGeneratorSettings,
        annotations: List<String>
    ): String {
        annotations.forEach {
            val fakedAnnotation = mutableSettings.getFakedAnnotationSetting(it)
            if (null !== fakedAnnotation && fakedAnnotation.fakedType.isNotEmpty()) {
                return@findFakedTypeByUnknownAnnotations fakedAnnotation.fakedType
            }
        }
        return ""
    }
}