package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.QueryHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo

class LocalQueryBusMainGenerator : AbstractLocalBusMainGenerator<QueryHandlerSetting>() {
    override fun findTarget(settings: List<QueryHandlerSetting>, namespace: String?): ClassInfo {
        return Utility.findLocalQueryBusTarget(settings, namespace)
    }

    override fun buildClass(settings: List<QueryHandlerSetting>, target: ClassInfo): TypeSpec.Builder {
        val type = TypeSpec.classBuilder(target.className)
            .addSuperinterface(Framework.QueryBus)
            .addSuperinterface(
                Framework.LocalBusResolver.parameterizedBy(
                    Framework.Query.parameterizedBy(TypeVariableName.invoke("*")),
                    Framework.QueryHandler.parameterizedBy(
                        TypeVariableName.invoke("*"),
                        TypeVariableName.invoke("*")
                    )
                )
            )

        buildProcessFunction(type)
        buildGetVersioningStrategyFunction(type)
        buildResolveFunction(settings, type)
        if (factoryFnNames.isNotEmpty()) {
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
                .addTypeVariable(TypeVariableName.invoke("R: ${Framework.QueryResult}"))
                .returns(typeR)
                .addAnnotation(
                    AnnotationSpec.builder(Suppress::class)
                        .addMember("%S", "UNCHECKED_CAST")
                        .build()
                )
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("query", Framework.Query.parameterizedBy(typeR))
                .addCode(makeResolveCodeBlock("query") {
                    it.add("return handler.execute(query = query, message = null) as R\n")
                })
                .addCode("throw %T(query.toString())\n", Framework.QueryHandlerNotFoundException)
                .build()
        )
    }

    private fun buildGetVersioningStrategyFunction(type: TypeSpec.Builder) {
        type.addFunction(
            makeGetVersioningStrategyFunctionBuilder()
                .addParameter("query", Framework.Query.parameterizedBy(TypeVariableName.invoke("*")))
                .build()
        )
    }

    private fun buildResolveFunction(settings: List<QueryHandlerSetting>, type: TypeSpec.Builder) {
        val grouped = groupHandlers(settings) { it.query.fullName() }
        val code = makeVersionedResolveHandlerCodeBlock(grouped, type) {
            it.query.toClassName()
        }

        type.addFunction(
            FunSpec.builder("resolve")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("instance", Framework.Query.parameterizedBy(TypeVariableName.invoke("*")))
                .returns(
                    Framework.QueryHandler.parameterizedBy(
                        TypeVariableName.invoke("*"),
                        TypeVariableName.invoke("*")
                    ).copy(nullable = true)
                )
                .addCode(code)
                .build()
        )
    }
}