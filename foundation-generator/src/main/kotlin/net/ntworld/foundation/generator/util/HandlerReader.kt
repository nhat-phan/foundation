package net.ntworld.foundation.generator.util

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlinx.metadata.Flag
import kotlinx.metadata.KmType
import net.ntworld.foundation.generator.setting.HandlerSetting
import net.ntworld.foundation.generator.type.Constructor
import net.ntworld.foundation.generator.type.Parameter

object HandlerReader {
    fun findPrimaryConstructor(setting: HandlerSetting): Constructor? {
        val kmClass = KotlinMetadataReader.findKmClass(setting.metadata)
        if (null === kmClass) {
            return null
        }

        kmClass.constructors.forEach {
            val isPrimary = Flag.Constructor.IS_PRIMARY(it.flags)
            if (isPrimary) {
                return@findPrimaryConstructor Constructor(it.valueParameters.map { valueParameter ->
                    Parameter(
                        name = valueParameter.name,
                        type = findTypeName(valueParameter.type)
                    )
                })
            }
        }
        return null
    }

    private fun findTypeName(type: KmType?): TypeName {
        return if (null !== type)
            KotlinMetadataReader.convertKmTypeToTypeName(type)
        else
            Any::class.asTypeName().copy(nullable = true)
    }
}