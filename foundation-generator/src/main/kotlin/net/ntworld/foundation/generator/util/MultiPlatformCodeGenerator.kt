package net.ntworld.foundation.generator.util

import com.squareup.kotlinpoet.CodeBlock
import net.ntworld.foundation.generator.Framework

object MultiPlatformCodeGenerator {
    fun getInitDefaultFakerForJvm(): CodeBlock {
        val code = CodeBlock.builder()
        code.add("%T(%T())", Framework.JavaFakerWrapper, Framework.JavaFaker)

        return code.build()
    }

    fun getInitIdGeneratorForJvm(): CodeBlock {
        val code = CodeBlock.builder()
        code.add("%T", Framework.UUIDGenerator)

        return code.build()
    }

    fun getEnvironmentForJvm(variableName: String, defaultValue: String): CodeBlock {
        val code = CodeBlock.builder()
        code.add("%T.getenv(%S) ?: %S", Framework.JavaSystem, variableName, defaultValue)

        return code.build()
    }
}