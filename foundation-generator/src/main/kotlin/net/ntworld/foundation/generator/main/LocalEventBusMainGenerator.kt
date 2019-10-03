package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.EventHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo

class LocalEventBusMainGenerator : AbstractLocalBusMainGenerator<EventHandlerSetting>() {
    override fun findTarget(settings: List<EventHandlerSetting>, namespace: String?): ClassInfo {
        return Utility.findLocalEventBusTarget(settings, namespace)
    }

    override fun buildClass(settings: List<EventHandlerSetting>, target: ClassInfo): TypeSpec.Builder {
        val type = TypeSpec.classBuilder(target.className)
            .addSuperinterface(Framework.ResolvableEventBus)

        buildPublishFunction(type)
        buildProcessFunction(type)
        buildResolveFunction(settings, type)
        if (this.isAbstract) {
            type.addModifiers(KModifier.ABSTRACT)
        } else {
            type.addModifiers(KModifier.OPEN)
        }
        return type
    }

    internal fun getHandlersArrayTypeName(): TypeName {
        return ClassName(
            "kotlin",
            "Array"
        ).parameterizedBy(
            Framework.EventHandler.parameterizedBy(TypeVariableName.invoke("*"))
        )
    }

    internal fun buildPublishFunction(type: TypeSpec.Builder) {
        type.addFunction(
            FunSpec.builder("publish")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("event", Framework.Event)
                .addStatement("this.process(event)")
                .build()
        )
    }

    internal fun buildProcessFunction(type: TypeSpec.Builder) {
        type.addFunction(
            FunSpec.builder("process")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("event", Framework.Event)
                .addCode(
                    CodeBlock.builder()
                        .add("val handlers = this.resolve(event)\n")
                        .beginControlFlow("if (null !== handlers)")
                        .add("handlers.forEach { it.execute(event = event, message = null) }\n")
                        .endControlFlow()
                        .build()
                )
                .build()
        )
    }

    internal fun buildResolveFunction(settings: List<EventHandlerSetting>, type: TypeSpec.Builder) {
        val grouped = groupHandlers(settings) { it.event.fullName() }

        val code = CodeBlock.builder()
        code.beginControlFlow("return when (instance)")

        grouped.forEach {
            code.add("is %T -> arrayOf(\n", it.value.first().event.toClassName())
            code.indent()
            buildCodeToResolveHandlers(it.value, code, type)
            code.unindent()
            code.add(")\n")
            code.add("\n")
        }

        code.add("else -> null\n")
        code.endControlFlow()

        type.addFunction(
            FunSpec.builder("resolve")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("instance", Framework.Event)
                .returns(getHandlersArrayTypeName().copy(nullable = true))
                .addCode(code.build())
                .build()
        )
    }

    internal fun buildCodeToResolveHandlers(
        settings: List<EventHandlerSetting>,
        code: CodeBlock.Builder,
        type: TypeSpec.Builder
    ) {
        settings.forEachIndexed { index, item ->
            buildCodeToResolveHandler(item, code, type)

            if (index != settings.lastIndex) {
                code.add(",")
            }
            code.add("\n")
        }
    }
}