package net.ntworld.foundation.generator.util

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.KOTLIN_NAMESPACE
import net.ntworld.foundation.generator.type.Constructor
import net.ntworld.foundation.generator.type.Parameter

/**
 * A class which collect a list of item's constructor and generate 1 constructor which
 * is composed item's constructor params, also contains some code generator utility.
 *
 * For example, if you have 2 items like:
 *
 * - First(userId: Int)
 * - Second(infrastructure: Infrastructure)
 *
 * This class will generate a composed constructor for a wrapper class:
 *
 * ```
 *   class Wrapper(private val userId: Int, private val infrastructure: Infrastructure) {
 *     fun resolveFirst() = First(userId)
 *     fun resolveSecond() = Second(infrastructure)
 *   }
 * ```
 *
 * In case there is any same parameter in item's constructor, the composed one can merged them
 * together.
 *
 * Please note that:
 *
 * - "same" parameter means they have same name and same type.
 * - order of parameters in composed constructor is first come first served based on .add()
 */
internal class ConstructorComposer {
    /**
     * Contains list of param for composed constructor
     */
    internal val composedParameters = mutableMapOf<String, TypeName>()

    /**
     * Contains added items
     */
    internal val items = mutableMapOf<String, Map<String, String>>()

    /**
     * Add constructor item by [resolvableName] and [constructor] type
     */
    fun add(resolvableName: String, constructor: Constructor) {
        val parameters = mutableMapOf<String, TypeName>()
        constructor.parameters.forEach {
            parameters[it.name] = it.type
        }
        items[resolvableName] = resolveComposedParameter(resolvableName, parameters)
    }

    /**
     * Resolve composedParameters for [itemName] which used for generate functions later on.
     */
    private fun resolveComposedParameter(
        itemName: String,
        parameters: Map<String, TypeName>
    ): Map<String, String> {
        return parameters.mapValues {
            val composedName = findComposedName(itemName, it.key, it.value)
            composedParameters[composedName] = it.value

            return@mapValues composedName
        }
    }

    /**
     * If [parameterType] is kotlin's type the composed name usually is [parameterName], in case it already registered
     * under [parameterName] but in difference [parameterType] then composed name should be [parameterName]For[itemName]
     *
     * If [parameterType] is not kotlin's type, it should be grouped by [parameterType] not [parameterName], for example
     *
     *   - First Item has parameter infrastructure: InfrastructureProvider
     *   - Second Item has parameter infrastructureProvider: InfrastructureProvider
     *
     * Then composedName should be infrastructure with type "InfrastructureProvider"
     */
    private fun findComposedName(itemName: String, parameterName: String, parameterType: TypeName): String {
        val registered = composedParameters[parameterName]
        if (null !== registered && registered != parameterType) {
            return "${parameterName}For$itemName"
        }

        if (!parameterType.toString().startsWith(KOTLIN_NAMESPACE)) {
            for (entry in composedParameters) {
                if (entry.value == parameterType) {
                    return entry.key
                }
            }
        }

        return parameterName
    }

    /**
     * Get composed Constructor
     */
    fun getComposedConstructor(): Constructor {
        return Constructor(
            parameters = composedParameters.map {
                Parameter(name = it.key, type = it.value)
            }
        )
    }

    /**
     * Generate composed constructor for given [type]
     */
    fun generateComposedConstructor(type: TypeSpec.Builder) {
        val constructor = FunSpec.constructorBuilder()

        composedParameters.forEach {
            constructor.addParameter(it.key, it.value)
            type.addProperty(
                PropertySpec.builder(it.key, it.value)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer(it.key)
                    .build()
            )
        }

        type.primaryConstructor(constructor.build())
    }

    /**
     * Generate an invoke constructor code block for given [resolvableName]
     *
     * Please note that generated code not included [resolvableName], just the part inside "(...)"
     */
    fun generateNewInstanceCodeBlockFor(resolvableName: String): CodeBlock {
        val item = items[resolvableName]
        if (null === item || item.isEmpty()) {
            return CodeBlock.of("()")
        }

        val code = CodeBlock.builder()
        code.add("(\n")
        code.indent()
        val lastIndex = item.size - 1
        item.keys.forEachIndexed { index, name ->
            code.add("%L = this.%L", name, item[name]!!)
            if (index != lastIndex) {
                code.add(",")
            }
            code.add("\n")
        }
        code.unindent()
        code.add(")")
        return code.build()
    }
}