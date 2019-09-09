package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.type.ClassInfo

data class GeneratedFile(
    val type: Type,
    val target: ClassInfo,
    val directory: String,
    val fileName: String,
    val path: String,
    val content: String,
    val empty: Boolean = false
) {
    enum class Type {
        Main,
        Test
    }

    companion object {
        fun makeMainFile(target: ClassInfo, content: String) = make(target, content, Type.Main)
        fun makeTestFile(target: ClassInfo, content: String) = make(target, content, Type.Test)

        fun makeEmptyMainFile(target: ClassInfo) = makeEmpty(target, Type.Main)
        fun makeEmptyTestFile(target: ClassInfo) = makeEmpty(target, Type.Test)

        private fun makeEmpty(target: ClassInfo, type: Type): GeneratedFile {
            val directory = target.packageName.replace(".", "/")
            val fileName = target.className + ".kt"
            return GeneratedFile(
                type = type,
                target = target,
                directory = "/$directory",
                fileName = fileName,
                path = "/$directory/$fileName",
                content = "",
                empty = true
            )
        }

        private fun make(target: ClassInfo, content: String, type: Type): GeneratedFile {
            val directory = target.packageName.replace(".", "/")
            val fileName = target.className + ".kt"
            return GeneratedFile(
                type = type,
                target = target,
                directory = "/$directory",
                fileName = fileName,
                path = "/$directory/$fileName",
                content = content,
                empty = false
            )
        }
    }
}