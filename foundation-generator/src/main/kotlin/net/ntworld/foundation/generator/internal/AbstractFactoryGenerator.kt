package net.ntworld.foundation.generator.internal

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.setting.AggregateFactorySetting

internal object AbstractFactoryGenerator {
    internal fun buildType(type: TypeSpec.Builder, setting: AggregateFactorySetting) {
        addConstructor(type)
        addAbstractMethods(type, setting)
        addGenerateFunction(type, setting)
        addRetrieveOrNullFunction(type, setting)
    }

    internal fun addConstructor(type: TypeSpec.Builder) {
        type.primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("infrastructure", Framework.Infrastructure)
                .build()
        )
        type.addProperty(
            PropertySpec.builder("infrastructure", Framework.Infrastructure)
                .addModifiers(KModifier.PRIVATE)
                .initializer("infrastructure")
                .build()
        )
    }

    internal fun addAbstractMethods(type: TypeSpec.Builder, setting: AggregateFactorySetting) {
        type.addModifiers(KModifier.ABSTRACT)
        type.addFunction(
            FunSpec.builder("make")
                .addModifiers(KModifier.ABSTRACT)
                .returns(setting.implementation.toClassName())
                .addParameter("id", String::class)
                .addParameter("isGenerated", Boolean::class)
                .build()
        )

        type.addFunction(
            FunSpec.builder("make")
                .addModifiers(KModifier.ABSTRACT)
                .addModifiers(KModifier.OVERRIDE)
                .returns(setting.implementation.toClassName())
                .addParameter("state", setting.state.toClassName())
                .build()
        )
    }

    internal fun addGenerateFunction(type: TypeSpec.Builder, setting: AggregateFactorySetting) {
        type.addFunction(
            FunSpec.builder("generate")
                .addModifiers(KModifier.OVERRIDE)
                .returns(setting.implementation.toClassName())
                .addCode(
                    CodeBlock.builder()
                        .add(
                            "val generator = this.infrastructure.root.idGeneratorOf(%T::class)\n",
                            setting.aggregate.toClassName()
                        )
                        .add("return make(generator.generate(), true)\n")
                        .build()
                )
                .build()
        )
    }

    internal fun addRetrieveOrNullFunction(type: TypeSpec.Builder, setting: AggregateFactorySetting) {
        val code = CodeBlock.builder()
            .add("val store = this.infrastructure.root.storeOf(%T::class)\n", setting.aggregate.toClassName())
            .add("val data = store.findById(id)\n")
            .add("if (null === data) {\n")
            .indent()
            .add("return null\n")
            .unindent()
            .add("}\n")
            .add("return make(data)\n")

        type.addFunction(
            FunSpec.builder("retrieveOrNull")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("id", String::class)
                .returns(setting.implementation.toClassNameNullable())
                .addCode(code.build())
                .build()
        )
    }
}