package net.ntworld.foundation.generator.internal

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.setting.AggregateFactorySetting

internal object WrapperFactoryWithEventSourcedGenerator {
    internal fun buildType(type: TypeSpec.Builder, setting: AggregateFactorySetting) {
        addConstructor(type, setting)
        addMakeMethod(type, setting)
        addGenerateFunction(type, setting)
        addRetrieveOrNullFunction(type, setting)
    }

    internal fun addConstructor(type: TypeSpec.Builder, setting: AggregateFactorySetting) {
        val wrappee = Framework.AggregateFactory.parameterizedBy(
            setting.aggregate.toClassName(),
            setting.state.toClassName()
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

    internal fun addMakeMethod(type: TypeSpec.Builder, setting: AggregateFactorySetting) {
        type.addFunction(
            FunSpec.builder("make")
                .addModifiers(KModifier.OVERRIDE)
                .returns(setting.implementation.toClassName())
                .addParameter("state", setting.state.toClassName())
                .addCode(
                    CodeBlock.of("return %T(base.make(state))\n", setting.implementation.toClassName())
                )
                .build()
        )
    }

    internal fun addGenerateFunction(type: TypeSpec.Builder, setting: AggregateFactorySetting) {
        type.addFunction(
            FunSpec.builder("generate")
                .addModifiers(KModifier.OVERRIDE)
                .returns(setting.implementation.toClassName())
                .addCode(
                    CodeBlock.of("return %T(base.generate())\n", setting.implementation.toClassName())
                )
                .build()
        )
    }

    internal fun addRetrieveOrNullFunction(type: TypeSpec.Builder, setting: AggregateFactorySetting) {
        val code = CodeBlock.builder()
            .add("return %T.retrieveOrNull(\n", Framework.EventSourcedFactory)
            .indent()
            .add("infrastructure = infrastructure,\n")
            .add("aggregateKlass = %T::class,\n", setting.aggregate.toClassName())
            .add("aggregateId = id,\n")
            .add("eventSourcedMaker = {\n")

            .indent()
            .add(
                "if (null === it) %T(base.generate()) else %T(base.make(it))\n",
                setting.implementation.toClassName(),
                setting.implementation.toClassName()
            )
            .unindent()

            .add("}\n")
            .unindent()
            .add(")\n")

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