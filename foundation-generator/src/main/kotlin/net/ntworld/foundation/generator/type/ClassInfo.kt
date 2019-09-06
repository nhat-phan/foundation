package net.ntworld.foundation.generator.type

import com.squareup.kotlinpoet.ClassName
import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.DEFAULT_COMPANION_OBJECT_NAME

@Serializable
data class ClassInfo(val className: String, val packageName: String) {
    fun toClassName(): ClassName {
        return ClassName(this.packageName, this.className)
    }

    fun toCompanionClassName(): ClassName {
        return ClassName(this.packageName, this.className, DEFAULT_COMPANION_OBJECT_NAME)
    }

    fun toClassNameNullable(): ClassName {
        return toClassName().copy(nullable = true)
    }

    fun fullName(): String {
        return "$packageName.$className"
    }
}
