package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorOutput
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.CommandHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo

class LocalCommandBusMainGenerator: AbstractLocalBusMainGenerator<CommandHandlerSetting>() {
    override fun findTarget(settings: List<CommandHandlerSetting>, namespace: String?): ClassInfo {
        return Utility.findLocalCommandBusTarget(settings, namespace)
    }

    override fun buildClass(settings: List<CommandHandlerSetting>, target: ClassInfo): TypeSpec.Builder {
        val type = TypeSpec.classBuilder(target.className)
            .addSuperinterface(Framework.CommandBus)
            .addSuperinterface(
                Framework.LocalBusResolver.parameterizedBy(
                    Framework.Command,
                    Framework.CommandHandler.parameterizedBy(TypeVariableName.invoke("*"))
                )
            )

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
        type.addFunction(
            FunSpec.builder("process")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("command", Framework.Command)
                .addCode(makeResolveCodeBlock("command") {
                    it.add("handler.execute(command = command, message = null)\n")
                })
                .build()
        )
    }

    private fun buildGetVersioningStrategyFunction(type: TypeSpec.Builder) {
        type.addFunction(
            makeGetVersioningStrategyFunctionBuilder()
                .addParameter("command", Framework.Command)
                .build()
        )
    }

    private fun buildResolveFunction(settings: List<CommandHandlerSetting>, type: TypeSpec.Builder) {
        val grouped = groupHandlers(settings) { it.command.fullName() }
        val code = makeVersionedResolveHandlerCodeBlock(grouped, type) { it.command.toClassName() }

        type.addFunction(
            FunSpec.builder("resolve")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("instance", Framework.Command)
                .returns(Framework.CommandHandler.parameterizedBy(TypeVariableName.invoke("*")).copy(nullable = true))
                .addCode(code)
                .build()
        )
    }
}