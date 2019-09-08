package net.ntworld.foundation.generator.type

import com.squareup.kotlinpoet.TypeName

data class Property(
    val name: String,
    val order: Int,
    val type: TypeName,
    val hasBody: Boolean = false,
    val fakedType: String = ""
) {
    companion object {
        fun sortProperties(properties: Map<String, Property>): Map<String, Property> {
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
}