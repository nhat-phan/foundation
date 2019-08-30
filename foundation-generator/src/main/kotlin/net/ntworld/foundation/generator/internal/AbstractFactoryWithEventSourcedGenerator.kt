package net.ntworld.foundation.generator.internal

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.setting.AggregateFactorySetting

internal object AbstractFactoryWithEventSourcedGenerator {
    internal fun buildType(type: TypeSpec.Builder, setting: AggregateFactorySetting) {
        AbstractFactoryGenerator.addConstructor(type)
        AbstractFactoryGenerator.addAbstractMethods(type, setting)
        AbstractFactoryGenerator.addGenerateFunction(type, setting)
        addRetrieveOrNullFunction(type, setting)
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
                "if (null === it) make(id, true) else make(it)\n"
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