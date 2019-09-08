package net.ntworld.foundation.generator.util

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlinx.metadata.*
import kotlinx.metadata.jvm.KotlinClassMetadata
import net.ntworld.foundation.generator.type.KotlinMetadata

object KotlinMetadataReader {
    fun findKmClass(metadata: KotlinMetadata): KmClass? {
        val header = KotlinMetadata.toKotlinClassHeader(metadata)
        val kmMetadata = KotlinClassMetadata.read(header)
        if (null === kmMetadata || kmMetadata !is KotlinClassMetadata.Class) {
            return null
        }

        return kmMetadata.toKmClass()
    }

    fun findKmProperty(kmClass: KmClass?, name: String): KmProperty? {
        if (null === kmClass) {
            return null
        }

        return kmClass.properties.find {
            it.name == name
        }
    }

    fun isPropertyHasBody(kmProperty: KmProperty?): Boolean {
        if (null === kmProperty) {
            return false
        }

        return Flag.PropertyAccessor.IS_NOT_DEFAULT(kmProperty.getterFlags) &&
            !Flag.Common.HAS_ANNOTATIONS(kmProperty.getterFlags)
    }

    fun findTypeNameOfProperty(kmProperty: KmProperty?): TypeName {
        if (null === kmProperty) {
            return Any::class.asTypeName().copy(nullable = true)
        }
        return convertKmTypeToTypeName(kmProperty.returnType)
    }

    fun convertKmTypeToTypeName(type: KmType): TypeName {
        val classifier: KmClassifier.Class = type.classifier as? KmClassifier.Class ?: return Any::class.asTypeName()
        val isNullable = Flag.Type.IS_NULLABLE(type.flags)
        val baseType = stringToClassName(
            classifier.name.replace(
                '/',
                '.'
            )
        )
        if (type.arguments.isEmpty()) {
            return if (isNullable) baseType.copy(nullable = true) else baseType
        }
        val typeArguments = type.arguments.toList().map {
            convertKmTypeProjectionToTypeName(it)
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

    fun convertKmTypeProjectionToTypeName(projection: KmTypeProjection): TypeName {
        if (null === projection.variance || projection.variance == KmVariance.INVARIANT) {
            val type = projection.type
            if (null === type) {
                return Any::class.asTypeName().copy(nullable = true)
            }
            return convertKmTypeToTypeName(type)
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
}