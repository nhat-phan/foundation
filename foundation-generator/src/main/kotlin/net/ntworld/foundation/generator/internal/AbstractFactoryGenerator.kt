package net.ntworld.foundation.generator.internal

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.setting.AggregateFactorySettings

internal object AbstractFactoryGenerator {
    internal fun buildType(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
        addConstructor(type, settings)
        addAbstractMethods(type, settings)
        addGenerateFunction(type, settings)
        addRetrieveFunction(type, settings)
    }

    internal fun addConstructor(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
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

    internal fun addAbstractMethods(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
        type.addModifiers(KModifier.ABSTRACT)
        type.addFunction(
            FunSpec.builder("make")
                .addModifiers(KModifier.ABSTRACT)
                .returns(settings.implementation.toClassName())
                .addParameter("id", String::class)
                .addParameter("isGenerated", Boolean::class)
                .build()
        )

        type.addFunction(
            FunSpec.builder("make")
                .addModifiers(KModifier.ABSTRACT)
                .addModifiers(KModifier.OVERRIDE)
                .returns(settings.implementation.toClassName())
                .addParameter("state", settings.state.toClassName())
                .build()
        )
    }

    internal fun addGenerateFunction(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
        type.addFunction(
            FunSpec.builder("generate")
                .addModifiers(KModifier.OVERRIDE)
                .returns(settings.implementation.toClassName())
                .addCode(
                    CodeBlock.builder()
                        .add(
                            "val generator = this.infrastructure.root.idGeneratorOf(%T::class)\n",
                            settings.aggregate.toClassName()
                        )
                        .add("return make(generator.generate(), true)\n")
                        .build()
                )
                .build()
        )
    }

    internal fun addRetrieveFunction(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
        val code = CodeBlock.builder()
            .add("val store = this.infrastructure.root.storeOf(%T::class)\n", settings.aggregate.toClassName())
            .add("val data = store.findById(id)\n")
            .add("if (null === data) {\n")
            .indent()
            .add("return null\n")
            .unindent()
            .add("}\n")
            .add("return make(data)\n")

        type.addFunction(
            FunSpec.builder("retrieve")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("id", String::class)
                .returns(settings.implementation.toClassNameNullable())
                .addCode(code.build())
                .build()
        )
    }
}