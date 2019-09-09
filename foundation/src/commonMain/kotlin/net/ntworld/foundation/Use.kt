package net.ntworld.foundation


/**
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
 *         arg("foundation.processor.settings-class", "com.domain.ContractData")
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
 * Please note that [@Use] usually annotated a main class/function
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Use(val settings: String)
