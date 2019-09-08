package net.ntworld.foundation.generator.type

import com.squareup.kotlinpoet.TypeName

data class Parameter(
    val name: String,
    val type: TypeName
)