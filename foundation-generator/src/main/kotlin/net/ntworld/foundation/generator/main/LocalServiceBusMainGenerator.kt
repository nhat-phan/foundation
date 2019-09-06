package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorOutput
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.RequestHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo

class LocalServiceBusMainGenerator {
    private val factoryFnMap = mutableMapOf<RequestHandlerSetting, String>()
    private val factoryFnNames = mutableListOf<String>()

    fun generate(settings: List<RequestHandlerSetting>, namespace: String? = null): GeneratedFile {
        val target = Utility.findLocalServiceBusTarget(settings, namespace)
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildMainGeneratedFile(target, stringBuffer.toString())
    }

    private fun buildFile(settings: List<RequestHandlerSetting>, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(settings, target))

        return file.build()
    }

    private fun buildClass(settings: List<RequestHandlerSetting>, target: ClassInfo): TypeSpec {
        val type = TypeSpec.classBuilder(target.className)
            .addSuperinterface(Framework.ServiceBus)
            .addSuperinterface(
                Framework.LocalBusResolver.parameterizedBy(
                    Framework.Request.parameterizedBy(TypeVariableName.invoke("*")),
                    Framework.RequestHandler.parameterizedBy(
                        TypeVariableName.invoke("*"),
                        TypeVariableName.invoke("*")
                    )
                )
            )

        buildConstructor(type)
        buildProcessFunction(type)
        buildGetVersioningStrategyFunction(type)
        buildResolveFunction(settings, type)
        if (factoryFnNames.isNotEmpty()) {
            type.addModifiers(KModifier.ABSTRACT)
        } else {
            type.addModifiers(KModifier.OPEN)
        }
        return type.build()
    }

    private fun buildConstructor(type: TypeSpec.Builder) {
        type.primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("infrastructure", Framework.Infrastructure)
                .build()
        )
        type.addProperty(
            PropertySpec.builder("infrastructure", Framework.Infrastructure)
                .initializer("infrastructure")
                .build()
        )
    }

    private fun buildProcessFunction(type: TypeSpec.Builder) {
        val typeR = TypeVariableName.invoke("R")
        type.addFunction(
            FunSpec.builder("process")
                .addTypeVariable(TypeVariableName.invoke("R: ${Framework.Response}<*>"))
                .returns(Framework.ServiceBusProcessResult.parameterizedBy(typeR))
                .addAnnotation(
                    AnnotationSpec.builder(Suppress::class)
                        .addMember("%S", "UNCHECKED_CAST")
                        .build()
                )
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("request", Framework.Request.parameterizedBy(typeR))
                .addCode(
                    CodeBlock.builder()
                        .add("val handler = this.resolve(request)\n")
                        .beginControlFlow("if (null !== handler)")
                        .add("return handler.execute(request = request, message = null) as R\n")
                        .endControlFlow()
                        .add("throw %T(request.toString())\n",
                            Framework.RequestHandlerNotFoundException
                        )
                        .build()
                )
                .build()
        )
    }

    internal fun buildGetVersioningStrategyFunction(type: TypeSpec.Builder) {
        type.addFunction(
            FunSpec.builder("getVersioningStrategy")
                .addModifiers(KModifier.OPEN)
                .addParameter("request", Framework.Request.parameterizedBy(TypeVariableName.invoke("*")))
                .returns(Framework.HandlerVersioningStrategy)
                .addStatement("return %T.useLatestVersion",
                    Framework.HandlerVersioningStrategy
                )
                .build()
        )
    }

    internal fun groupHandlers(settings: List<RequestHandlerSetting>): Map<String, List<RequestHandlerSetting>> {
        val grouped = mutableMapOf<String, MutableList<RequestHandlerSetting>>()
        settings.forEach {
            val key = it.request.fullName()
            if (!grouped.containsKey(key)) {
                grouped[key] = mutableListOf()
            }
            grouped[key]!!.add(it)
        }
        return grouped
    }

    internal fun buildResolveFunction(settings: List<RequestHandlerSetting>, type: TypeSpec.Builder) {
        val grouped = groupHandlers(settings)

        val code = CodeBlock.builder()

        code.add("val strategy = getVersioningStrategy(instance)\n")
            .beginControlFlow("if (strategy.skip())")
            .add("return null\n")
            .endControlFlow()
            .add("\n")

        code.beginControlFlow("return when (instance)")

        grouped.forEach {
            if (it.value.size == 1) {
                val first = it.value.first()
                code.add("is %T -> ", first.request.toClassName())
                buildCodeToResolveHandler(first, code, type)
                code.add("\n")
            } else {
                code.beginControlFlow("is %T ->", it.value.first().request.toClassName())
                buildCodeToResolveVersioningHandler(it.value, code, type)
                code.endControlFlow()
            }
            code.add("\n")
        }

        code.add("else -> null\n")
        code.endControlFlow()

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
                .addCode(code.build())
                .build()
        )
    }

    internal fun buildCodeToResolveHandler(
        item: RequestHandlerSetting,
        code: CodeBlock.Builder,
        type: TypeSpec.Builder
    ) {
        if (!item.makeByFactory) {
            code.add("%T(infrastructure)", item.handler.toClassName())
            return
        }

        val (exist, factoryFnName) = this.findFactoryFunctionName(item)
        val fnName = "make$factoryFnName"
        if (!exist) {
            type.addFunction(
                FunSpec.builder(fnName)
                    .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
                    .returns(item.handler.toClassName())
                    .build()
            )
        }
        code.add("%L()", fnName)
    }

    internal fun buildCodeToResolveVersioningHandler(
        settings: List<RequestHandlerSetting>,
        code: CodeBlock.Builder,
        type: TypeSpec.Builder
    ) {
        val map = mutableMapOf<Int, RequestHandlerSetting>()
        var latestVersion: Int = Int.MIN_VALUE
        for (item in settings) {
            map[item.version] = item

            if (latestVersion < item.version) {
                latestVersion = item.version
            }
        }

        code.beginControlFlow("if (strategy.useLatestVersion())")
        code.add("return ")
        buildCodeToResolveHandler(map[latestVersion]!!, code, type)
        code.add("\n")
        code.endControlFlow()
        code.add("\n")

        code.beginControlFlow("return when (strategy.specificVersion)")
        map.keys.sorted().forEach {
            code.add("%L -> ", map[it]!!.version)
            buildCodeToResolveHandler(map[it]!!, code, type)
            code.add("\n")
        }
        code.add("else -> null\n")
        code.endControlFlow()
    }

    internal fun findFactoryFunctionName(setting: RequestHandlerSetting): Pair<Boolean, String> {
        if (factoryFnMap.contains(setting)) {
            return Pair(true, factoryFnMap[setting]!!)
        }

        val simpleName = setting.handler.className
        if (!factoryFnNames.contains(simpleName)) {
            factoryFnMap[setting] = simpleName
            factoryFnNames.add(simpleName)
            return Pair(false, simpleName)
        }
        factoryFnMap[setting] = "_${setting.handler.packageName.replace(".", "_")}_$simpleName"
        return Pair(false, factoryFnMap[setting]!!)
    }
}