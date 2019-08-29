package net.ntworld.foundation.test

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

data class Contract<T : Any>(
    val definition: KClass<T>,
    val implementation: KClass<T>? = null,
    val fields: List<KProperty1<T, *>>? = null
)
