package net.ntworld.foundation.generator.util

import net.ntworld.foundation.generator.DEFAULT_COMPANION_OBJECT_NAME
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.setting.FakedAnnotationSetting
import net.ntworld.foundation.generator.type.Property

class ContractReader(
    contractSettings: List<ContractSetting>,
    fakedAnnotationSettings: List<FakedAnnotationSetting>
) {
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
        val kmClass = KotlinMetadataReader.findKmClass(setting.metadata)
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
        return Property.sortProperties(result)
    }

    private fun readPropertyOfSetting(
        setting: ContractSetting,
        bucket: MutableMap<String, Property>
    ) {
        val orderOffset = bucket.size
        val kmClass = KotlinMetadataReader.findKmClass(setting.metadata)
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

            val kmProperty = KotlinMetadataReader.findKmProperty(kmClass, it.key)
            bucket[contractProperty.name] = Property(
                name = contractProperty.name,
                order = order,
                type = KotlinMetadataReader.findTypeNameOfProperty(kmProperty),
                hasBody = KotlinMetadataReader.isPropertyHasBody(kmProperty),
                fakedType = fakedType
            )
        }
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
}