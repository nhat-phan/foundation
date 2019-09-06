package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlinx.metadata.*
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.setting.FakedAnnotationSetting

class ContractReader(
    contractSettings: List<ContractSetting>,
    fakedAnnotationSettings: List<FakedAnnotationSetting>
) {
    data class Property(
        val name: String,
        val order: Int,
        val type: TypeName,
        val hasBody: Boolean = false,
        val fakedType: String = ""
    )

    private val settings: Map<String, ContractSetting> = mutableMapOf()
    private val fakedAnnotations: Map<String, FakedAnnotationSetting> = mutableMapOf()

    init {
        contractSettings.forEach {
            (settings as MutableMap)[it.name] = it
        }
        fakedAnnotationSettings.forEach {
            (fakedAnnotations as MutableMap)[it.name] = it
        }
    }

    fun hasCompanionObject(name: String): Boolean {
        val setting: ContractSetting = settings[name] ?: return false
        val kmClass = findKmClass(setting)
        if (null === kmClass) {
            return false
        }
        return null !== kmClass.companionObject && kmClass.companionObject == DEFAULT_COMPANION_OBJECT_NAME
    }

    fun findPropertiesOfContract(name: String): Map<String, Property>? {
        val setting: ContractSetting = settings[name] ?: return null
        val supertypeSettings = mutableMapOf<String, ContractSetting>()
        findSupertypeSettingsRecursively(setting, supertypeSettings)

        val result = mutableMapOf<String, Property>()

        supertypeSettings.forEach {
            readPropertyOfSetting(it.value, result)
        }
        readPropertyOfSetting(setting, result)
        return reorderProperties(result)
    }

    private fun readPropertyOfSetting(setting: ContractSetting, bucket: MutableMap<String, Property>) {
        val orderOffset = bucket.size
        val kmClass = findKmClass(setting)
        setting.properties.forEach {
            val contractProperty = it.value
            val previousItemInBucket = bucket[contractProperty.name]

            var fakedType = if (null !== previousItemInBucket) previousItemInBucket.fakedType else ""
            val order = orderOffset + contractProperty.order

            contractProperty.unknownAnnotations.forEach { unknownAnnotation ->
                if (this.fakedAnnotations.containsKey(unknownAnnotation)) {
                    fakedType = this.fakedAnnotations[unknownAnnotation]!!.fakedType
                }
            }

            if (contractProperty.hasFakedAnnotation && null !== contractProperty.fakedType && contractProperty.fakedType.isNotEmpty()) {
                fakedType = contractProperty.fakedType
            }

            val kmProperty = findKmProperty(kmClass, it.key)
            bucket[contractProperty.name] = Property(
                name = contractProperty.name,
                order = order,
                type = findTypeOfProperty(kmProperty),
                hasBody = isPropertyHasBody(kmProperty),
                fakedType = fakedType
            )
        }
    }

    private fun findKmClass(setting: ContractSetting): KmClass? {
        val header = makeKotlinMetadataHeader(setting)
        val metadata = KotlinClassMetadata.read(header)
        if (null === metadata || metadata !is KotlinClassMetadata.Class) {
            return null
        }

        val kmClass = metadata.toKmClass()
//        println("=============")
//        println(kmClass.companionObject)
//        println("=============")
        return kmClass
    }

    private fun findKmProperty(kmClass: KmClass?, name: String): KmProperty? {
        if (null === kmClass) {
            return null
        }

        return kmClass.properties.find {
            it.name == name
        }
    }

    private fun isPropertyHasBody(kmProperty: KmProperty?): Boolean {
        if (null === kmProperty) {
            return false
        }

        return Flag.PropertyAccessor.IS_NOT_DEFAULT(kmProperty.getterFlags) &&
            !Flag.Common.HAS_ANNOTATIONS(kmProperty.getterFlags)
    }

    private fun findTypeOfProperty(kmProperty: KmProperty?): TypeName {
        if (null === kmProperty) {
            return Any::class.asTypeName().copy(nullable = true)
        }
        return convertKmType(kmProperty.returnType)
    }

    private fun convertKmType(type: KmType): TypeName {
        val classifier: KmClassifier.Class = type.classifier as? KmClassifier.Class ?: return Any::class.asTypeName()
        val isNullable = Flag.Type.IS_NULLABLE(type.flags)
        val baseType = stringToClassName(classifier.name.replace('/', '.'))
        if (type.arguments.isEmpty()) {
            return if (isNullable) baseType.copy(nullable = true) else baseType
        }
        val typeArguments = type.arguments.toList().map {
            convertKmTypeProjection(it)
        }

        val typeArgumentsSpread = Array<TypeName>(typeArguments.size) {
            Any::class.asTypeName().copy(nullable = true)
        }
        typeArguments.forEachIndexed { index, it -> typeArgumentsSpread[index] = it }
        if (isNullable) {
            return baseType.parameterizedBy(*typeArgumentsSpread).copy(nullable = true)
        }
        return baseType.parameterizedBy(*typeArgumentsSpread)
    }

    private fun convertKmTypeProjection(projection: KmTypeProjection): TypeName {
        if (null === projection.variance || projection.variance == KmVariance.INVARIANT) {
            val type = projection.type
            if (null === type) {
                return Any::class.asTypeName().copy(nullable = true)
            }
            return convertKmType(type)
        }
        throw Exception("Not supported yet")
    }

    private fun stringToClassName(name: String): ClassName {
        val parts = name.split('.')
        if (parts.size == 1) {
            return ClassName("", name)
        }
        val packageParts = mutableListOf<String>()
        for (i in 0 until parts.lastIndex) {
            packageParts.add(parts[i])
        }
        return ClassName(packageParts.joinToString("."), parts[parts.lastIndex])
    }

    private fun makeKotlinMetadataHeader(setting: ContractSetting): KotlinClassHeader {
        var metadataVersion: IntArray? = null
        if (null !== setting.metadata.metadataVersion) {
            metadataVersion = IntArray(setting.metadata.metadataVersion.size) { 0 }
            setting.metadata.metadataVersion.forEachIndexed { index, item ->
                metadataVersion[index] = item
            }
        }

        var bytecodeVersion: IntArray? = null
        if (null !== setting.metadata.bytecodeVersion) {
            bytecodeVersion = IntArray(setting.metadata.bytecodeVersion.size) { 0 }
            setting.metadata.bytecodeVersion.forEachIndexed { index, item ->
                bytecodeVersion[index] = item
            }
        }

        var data1: Array<String>? = null
        if (null !== setting.metadata.data1) {
            data1 = Array(setting.metadata.data1.size) { "" }
            setting.metadata.data1.forEachIndexed { index, item ->
                data1[index] = item
            }
        }

        var data2: Array<String>? = null
        if (null !== setting.metadata.data2) {
            data2 = Array(setting.metadata.data2.size) { "" }
            setting.metadata.data2.forEachIndexed { index, item ->
                data2[index] = item
            }
        }

        return KotlinClassHeader(
            data1 = data1,
            data2 = data2,
            bytecodeVersion = bytecodeVersion,
            extraInt = setting.metadata.extraInt,
            extraString = setting.metadata.extraString,
            kind = setting.metadata.kind,
            metadataVersion = metadataVersion,
            packageName = setting.metadata.packageName
        )
    }

    private fun findSupertypeSettingsRecursively(
        setting: ContractSetting,
        bucket: MutableMap<String, ContractSetting>
    ) {
        if (null === setting.supertypes || setting.supertypes.isEmpty()) {
            return
        }
        for (supertype in setting.supertypes) {
            val supertypeSetting = settings[supertype]
            if (null !== supertypeSetting) {
                bucket[supertype] = supertypeSetting
                findSupertypeSettingsRecursively(supertypeSetting, bucket)
            }
        }
    }

    private fun reorderProperties(properties: Map<String, Property>): Map<String, Property> {
        val keys = properties.keys.toList()
        val order = properties.mapValues { it.value.order }
        val sortedKeys = keys.sortedWith(Comparator { o1, o2 -> order[o1]!!.compareTo(order[o2]!!) })
        val result = mutableMapOf<String, Property>()
        var index = 1
        for (key in sortedKeys) {
            result[key] = properties[key]!!.copy(order = index)
            index++
        }
        return result
    }
}