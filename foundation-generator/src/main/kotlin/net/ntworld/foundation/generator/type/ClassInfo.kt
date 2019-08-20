package net.ntworld.foundation.generator.type

import com.squareup.kotlinpoet.ClassName
import kotlinx.serialization.Serializable

@Serializable
data class ClassInfo(val className: String, val packageName: String) {
    fun toClassName(): ClassName {
        return ClassName(this.packageName, this.className)
    }

    fun toClassNameNullable(): ClassName {
        return toClassName().copy(nullable = true)
    }
}
