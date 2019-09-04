package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.type.ClassInfo

data class GeneratedFile(
    val type: Type,
    val target: ClassInfo,
    val directory: String,
    val fileName: String,
    val path: String,
    val content: String
) {
    enum class Type {
        Main,
        Test
    }
}