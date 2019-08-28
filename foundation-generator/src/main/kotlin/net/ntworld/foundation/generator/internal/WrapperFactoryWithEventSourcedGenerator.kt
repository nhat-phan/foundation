package net.ntworld.foundation.generator.internal

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.setting.AggregateFactorySettings

internal object WrapperFactoryWithEventSourcedGenerator {
    internal fun buildType(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
        addConstructor(type, settings)
        addMakeMethod(type, settings)
        addGenerateFunction(type, settings)
        addRetrieveOrNullFunction(type, settings)
    }

    internal fun addConstructor(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
        val wrappee = Framework.AggregateFactory.parameterizedBy(
            settings.aggregate.toClassName(),
            settings.state.toClassName()
        )
        type.primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("base", wrappee)
                .addParameter("infrastructure", Framework.Infrastructure)
                .build()
        )
        type.addProperty(
            PropertySpec.builder("base", wrappee)
                .addModifiers(KModifier.PRIVATE)
                .initializer("base")
                .build()
        )
        type.addProperty(
            PropertySpec.builder("infrastructure", Framework.Infrastructure)
                .addModifiers(KModifier.PRIVATE)
                .initializer("infrastructure")
                .build()
        )
    }

    internal fun addMakeMethod(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
        type.addFunction(
            FunSpec.builder("make")
                .addModifiers(KModifier.OVERRIDE)
                .returns(settings.implementation.toClassName())
                .addParameter("state", settings.state.toClassName())
                .addCode(
                    CodeBlock.of("return %T(base.make(state))\n", settings.implementation.toClassName())
                )
                .build()
        )
    }

    internal fun addGenerateFunction(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
        type.addFunction(
            FunSpec.builder("generate")
                .addModifiers(KModifier.OVERRIDE)
                .returns(settings.implementation.toClassName())
                .addCode(
                    CodeBlock.of("return %T(base.generate())\n", settings.implementation.toClassName())
                )
                .build()
        )
    }

    internal fun addRetrieveOrNullFunction(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
        val code = CodeBlock.builder()
            .add("return %T.retrieveOrNull(\n", Framework.EventSourcedFactory)
            .indent()
            .add("infrastructure = infrastructure,\n")
            .add("aggregateKlass = %T::class,\n", settings.aggregate.toClassName())
            .add("aggregateId = id,\n")
            .add("eventSourcedMaker = {\n")

            .indent()
            .add(
                "if (null === it) %T(base.generate()) else %T(base.make(it))\n",
                settings.implementation.toClassName(),
                settings.implementation.toClassName()
            )
            .unindent()

            .add("}\n")
            .unindent()
            .add(")\n")

        type.addFunction(
            FunSpec.builder("retrieveOrNull")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("id", String::class)
                .returns(settings.implementation.toClassNameNullable())
                .addCode(code.build())
                .build()
        )
    }
}