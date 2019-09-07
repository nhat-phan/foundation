package net.ntworld.foundation.processor.util

import net.ntworld.foundation.Faked
import net.ntworld.foundation.generator.type.ContractProperty
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

object ContractPropertyUtil {
    fun makeByElement(element: Element): ContractProperty {
        val annotations = element.annotationMirrors
        val unknownAnnotations = mutableListOf<String>()
        var hasFakedAnnotation = false
        var fakedType: String? = null
        var hasNotNullAnnotation = false
        var hasNullableAnnotation = false
        annotations.forEach {
            val annotationElement = it.annotationType.asElement() as? TypeElement ?: return@forEach
            val qualifiedName = annotationElement.qualifiedName.toString()

            if (qualifiedName == NotNull::class.java.canonicalName) {
                hasNotNullAnnotation = true
                return@forEach
            }

            if (qualifiedName == Nullable::class.java.canonicalName) {
                hasNullableAnnotation = true
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
            hasNullableAnnotation = hasNullableAnnotation,
            hasFakedAnnotation = hasFakedAnnotation,
            fakedType = fakedType
        )
    }


    fun sortProperties(properties: Map<String, ContractProperty>): Map<String, ContractProperty> {
        val keys = properties.keys.toList()
        val order = properties.mapValues { it.value.order }
        val sortedKeys = keys.sortedWith(Comparator { o1, o2 -> order[o1]!!.compareTo(order[o2]!!) })
        val result = mutableMapOf<String, ContractProperty>()
        var index = 1
        for (key in sortedKeys) {
            result[key] = properties[key]!!.copy(order = index)
            index++
        }
        return result
    }
}