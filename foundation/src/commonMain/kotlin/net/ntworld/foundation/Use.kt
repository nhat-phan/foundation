package net.ntworld.foundation

import kotlin.reflect.KClass


/**
 * There are 2 ways to use [@Use]
 *
 * ***@Use(contract = ...)***
 *
 * In case you want to generate code from contracts which have no [@Handler], you need
 * to use [@Use] with [contract].
 *
 * Please note that [@Use] with [contract] usually annotated a function which uses [contract]
 *
 * ***@Use(settings = "...settings...")***
 *
 * Annotation processing has limitation that it cannot process compiled library
 * then this annotation will tell foundation-processor read and merged given
 * [settings] from a compiled library.
 *
 *
 * **Usage**
 *
 * In your library, please use kapt("...foundation-processor") with options:
 *
 * ```kotlin
 *
 * kapt {
 *     arguments {
 *         arg("foundation.processor.mode", "contract-only")
 *         arg("foundation.processor.settingsClass", "com.domain.ContractData")
 *     }
 * }
 * ```
 *
 * `foundation-processor` will generate `settings` constant in `com.domain.ContractData`
 *
 * Then in implementation project you can use [@Use] to load settings.
 *
 * ```kotlin
 *
 * @Use(com.domain.ContractData.settings)
 * fun main() {
 *   ...
 * }
 *
 * ```
 *
 * Please note that [@Use] with [settings] usually annotated a main class/function
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Use(
    val settings: String = "",
    val contract: KClass<out Contract> = Contract::class
)