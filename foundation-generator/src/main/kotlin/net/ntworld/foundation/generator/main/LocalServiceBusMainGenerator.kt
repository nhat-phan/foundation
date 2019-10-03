package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.RequestHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo

class LocalServiceBusMainGenerator : AbstractLocalBusMainGenerator<RequestHandlerSetting>() {
    override fun findTarget(settings: List<RequestHandlerSetting>, namespace: String?): ClassInfo {
        return Utility.findLocalServiceBusTarget(settings, namespace)
    }

    override fun buildClass(settings: List<RequestHandlerSetting>, target: ClassInfo): TypeSpec.Builder {
        val type = TypeSpec.classBuilder(target.className)
            .addSuperinterface(Framework.ResolvableServiceBus)

        buildProcessFunction(type)
        buildGetVersioningStrategyFunction(type)
        buildResolveFunction(settings, type)
        if (this.isAbstract) {
            type.addModifiers(KModifier.ABSTRACT)
        } else {
            type.addModifiers(KModifier.OPEN)
        }
        return type
    }

    private fun buildProcessFunction(type: TypeSpec.Builder) {
        val typeR = TypeVariableName.invoke("R")
        type.addFunction(
            FunSpec.builder("process")
                .addTypeVariable(TypeVariableName.invoke("R: ${Framework.Response}"))
                .returns(Framework.ServiceBusProcessResult.parameterizedBy(typeR))
                .addAnnotation(
                    AnnotationSpec.builder(Suppress::class)
                        .addMember("%S", "UNCHECKED_CAST")
                        .build()
                )
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("request", Framework.Request.parameterizedBy(typeR))
                .addCode(makeResolveCodeBlock("request") {
                    it.add("return ServiceBusProcessResult.make(handler.execute(request = request, message = null) as R)\n")
                })
                .addCode("throw %T(request.toString())\n", Framework.RequestHandlerNotFoundException)
                .build()
        )
    }

    private fun buildGetVersioningStrategyFunction(type: TypeSpec.Builder) {
        type.addFunction(
            makeGetVersioningStrategyFunctionBuilder()
                .addParameter("request", Framework.Request.parameterizedBy(TypeVariableName.invoke("*")))
                .build()
        )
    }

    private fun buildResolveFunction(settings: List<RequestHandlerSetting>, type: TypeSpec.Builder) {
        val grouped = groupHandlers(settings) { it.request.fullName() }
        val code = makeVersionedResolveHandlerCodeBlock(grouped, type) {
            it.request.toClassName()
        }

        type.addFunction(
            FunSpec.builder("resolve")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("instance", Framework.Request.parameterizedBy(TypeVariableName.invoke("*")))
                .returns(
                    Framework.RequestHandler.parameterizedBy(
                        TypeVariableName.invoke("*"),
                        TypeVariableName.invoke("*")
                    ).copy(nullable = true)
                )
                .addCode(code)
                .build()
        )
    }
}