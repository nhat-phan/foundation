package net.ntworld.foundation.generator.internal

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.setting.AggregateFactorySettings

internal object AbstractFactoryWithEventSourcedGenerator {
    internal fun buildType(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
        AbstractFactoryGenerator.addConstructor(type)
        AbstractFactoryGenerator.addAbstractMethods(type, settings)
        AbstractFactoryGenerator.addGenerateFunction(type, settings)
        addRetrieveFunction(type, settings)
    }

    internal fun addRetrieveFunction(type: TypeSpec.Builder, settings: AggregateFactorySettings) {
        val code = CodeBlock.builder()
            .add("return %T.retrieve(\n", Framework.EventSourcedFactory)
            .indent()
            .add("infrastructure = infrastructure,\n")
            .add("aggregateKlass = %T::class,\n", settings.aggregate.toClassName())
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
            FunSpec.builder("retrieve")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("id", String::class)
                .returns(settings.implementation.toClassNameNullable())
                .addCode(code.build())
                .build()
        )
    }
}