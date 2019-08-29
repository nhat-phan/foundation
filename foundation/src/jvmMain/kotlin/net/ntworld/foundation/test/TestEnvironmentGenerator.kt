package net.ntworld.foundation.test

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.Faked
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

actual object TestEnvironmentGenerator {
    internal data class PropertyData(
        val property: KProperty<*>,
        val hasFaked: Boolean,
        val fakedType: String
    )

    internal val privateImplNames = mutableListOf<String>()

    actual fun generateContractFactory(
        packageName: String,
        className: String,
        contracts: Map<String, Contract<*>>
    ): String {
        val file = FileSpec.builder(packageName, className)
        val type = TypeSpec.classBuilder(className)
        buildReadMapFunction(type)
        buildCreateFakedDataFunction(type)

        contracts.forEach { t, u -> generateContract(t, u, packageName, className, type) }

        val stringBuffer = StringBuffer()
        file
            .addType(type.build())
            .build()
            .writeTo(stringBuffer)
        return stringBuffer.toString()
    }

    internal fun buildCreateFakedDataFunction(type: TypeSpec.Builder) {
        type.addFunction(
            FunSpec.builder("createFakedData")
                .addTypeVariable(TypeVariableName.invoke("T"))
                .addParameter("type", String::class)
                .returns(TypeVariableName.invoke("T"))
                .addAnnotation(
                    AnnotationSpec.builder(Suppress::class)
                        .addMember("%S", "UNCHECKED_CAST")
                        .build()
                )
                .addCode("return faker.makeFakeData(type) as T\n")
                .build()
        )
    }

    internal fun buildReadMapFunction(type: TypeSpec.Builder) {
        val code = CodeBlock.builder()
        code.add("return if (data.containsKey(key)) data[key] as T else faker.makeFakeData(fakerType) as T\n")

        type.addFunction(
            FunSpec.builder("readMap")
                .addTypeVariable(TypeVariableName.invoke("T"))
                .addParameter("data", Map::class.parameterizedBy(String::class, Any::class))
                .addParameter("key", String::class)
                .addParameter("fakerType", String::class)
                .returns(TypeVariableName.invoke("T"))
                .addAnnotation(
                    AnnotationSpec.builder(Suppress::class)
                        .addMember("%S", "UNCHECKED_CAST")
                        .build()
                )
                .addCode(code.build())
                .build()
        )
    }

    internal fun generateContract(
        name: String,
        contract: Contract<*>,
        packageName: String,
        className: String,
        type: TypeSpec.Builder
    ) {
        val properties = getOrderedProperties(contract.definition, contract.fields)
        val impl = if (null === contract.implementation) {
            ClassName(
                packageName = "$packageName.$className",
                simpleName = createImplementationIfNeeded(properties, contract, type)
            )
        } else {
            contract.implementation.asClassName()
        }

        val requiredProperties = (properties.filter { !it.hasFaked }).toMutableList()
        val optionalProperties = (properties.filter { it.hasFaked }).toMutableList()
        if (optionalProperties.isEmpty()) {
            createMakeFunction(name, contract, impl, type, requiredProperties, optionalProperties, true)
        } else {
            // make() with data
            createMakeFunction(name, contract, impl, type, requiredProperties, optionalProperties, true)

            // make() with shifted params
            while(optionalProperties.isNotEmpty()) {
                createMakeFunction(name, contract, impl, type, requiredProperties, optionalProperties, false)
                requiredProperties.add(optionalProperties.removeAt(0))
            }

            // make() with full version
            createMakeFunction(name, contract, impl, type, requiredProperties, listOf(), false)
        }
    }

    internal fun createMakeFunction(
        name: String,
        contract: Contract<*>,
        impl: ClassName,
        type: TypeSpec.Builder,
        requiredProperties: List<PropertyData>,
        optionalProperties: List<PropertyData>,
        usingReadData: Boolean
    ) {
        val makeWithData = FunSpec.builder("make$name").returns(contract.definition)
        requiredProperties.forEach { item ->
            makeWithData.addParameter(item.property.name, getReturnTypeOf(item.property))
        }
        if (optionalProperties.isNotEmpty() && usingReadData) {
            makeWithData.addParameter("data", Map::class.parameterizedBy(String::class, Any::class))
        }

        val code = CodeBlock.builder()
        code.add("return %T(\n", impl)
        code.indent()
        requiredProperties.forEachIndexed { index, item ->
            code.add("%L = %L", item.property.name, item.property.name)

            if (index != requiredProperties.lastIndex || optionalProperties.isNotEmpty()) {
                code.add(",")
            }
            code.add("\n")
        }
        optionalProperties.forEachIndexed { index, item ->
            if (usingReadData) {
                code.add(
                    "%L = readMap(data, %S, %S)",
                    item.property.name,
                    item.property.name,
                    item.fakedType
                )
            } else {
                code.add(
                    "%L = createFakedData(%S)",
                    item.property.name,
                    item.fakedType
                )
            }

            if (index != optionalProperties.lastIndex) {
                code.add(",")
            }
            code.add("\n")
        }
        code.unindent()
        code.add(")\n", impl)
        makeWithData.addCode(code.build())

        type.addFunction(makeWithData.build())
    }

    internal fun createImplementationIfNeeded(
        properties: List<PropertyData>,
        contract: Contract<*>,
        type: TypeSpec.Builder
    ): String {
        val implName = findImplementationName(contract.definition)
        if (!privateImplNames.contains(implName)) {
            val impl = TypeSpec.classBuilder(implName)
                .addModifiers(KModifier.PRIVATE, KModifier.DATA)
                .addSuperinterface(contract.definition)
            val ctor = FunSpec.constructorBuilder()

            properties.forEach {
                ctor.addParameter(it.property.name, getReturnTypeOf(it.property))
                impl.addProperty(
                    PropertySpec.builder(it.property.name, getReturnTypeOf(it.property))
                        .addModifiers(KModifier.OVERRIDE)
                        .initializer(it.property.name)
                        .build()
                )
            }
            impl.primaryConstructor(ctor.build())

            type.addType(impl.build())
            privateImplNames.add(implName)
        }
        return implName
    }

    internal fun getReturnTypeOf(property: KProperty<*>): TypeName {
        val nullable = property.returnType.isMarkedNullable
        if (nullable) {
            return property.returnType.asTypeName().copy(nullable = true)
        }
        return property.returnType.asTypeName()
    }

    internal fun getOrderedProperties(definition: KClass<*>, fields: List<KProperty1<*, *>>?): List<PropertyData> {
        val props = getProperties(definition)
        val result = props.map {
            val annotation = it.findAnnotation<Faked>()
            if (null !== annotation && annotation.type.isNotEmpty()) {
                PropertyData(property = it, hasFaked = true, fakedType = annotation.type)
            } else {
                PropertyData(property = it, hasFaked = false, fakedType = "")
            }
        }
        val ordering = this.findOrderingOfProperties(props, fields)

        return result.sortedWith(Comparator { o1, o2 ->
            if (o1!!.hasFaked == o2.hasFaked) {
                ordering.indexOf(o1!!.property.name).compareTo(ordering.indexOf(o2!!.property.name))
            } else {
                if (o1.hasFaked) 1 else -1
            }
        })
    }

    internal fun findOrderingOfProperties(props: List<KProperty<*>>, fields: List<KProperty1<*, *>>?): List<String> {
        if (null !== fields) {
            return fields.map { it.name }
        }

        // TODO: auto detect ordering by props
        return props.map { it.name }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun getProperties(definition: KClass<*>): List<KProperty<*>> {
        return definition.members.filter { it is KProperty } as List<KProperty<*>>
    }

    internal fun findImplementationName(definition: KClass<*>): String {
        return "${definition.simpleName}TestImpl"
    }
}