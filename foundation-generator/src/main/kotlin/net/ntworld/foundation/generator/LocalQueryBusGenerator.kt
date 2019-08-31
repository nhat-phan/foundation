package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.setting.QueryHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo

class LocalQueryBusGenerator {
    private val factoryFnMap = mutableMapOf<QueryHandlerSetting, String>()
    private val factoryFnNames = mutableListOf<String>()

    fun generate(settings: List<QueryHandlerSetting>, namespace: String? = null): GeneratedFile {
        val target = Utility.findLocalQueryBusTarget(settings, namespace)
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    internal fun buildFile(settings: List<QueryHandlerSetting>, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(settings, target))

        return file.build()
    }

    internal fun buildClass(settings: List<QueryHandlerSetting>, target: ClassInfo): TypeSpec {
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

        buildConstructor(type)
        buildProcessFunction(type)
        buildGetVersioningStrategyFunction(type)
        buildResolveFunction(settings, type)
        if (factoryFnNames.isNotEmpty()) {
            type.addModifiers(KModifier.ABSTRACT)
        }
        return type.build()
    }

    internal fun buildConstructor(type: TypeSpec.Builder) {
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

    internal fun buildProcessFunction(type: TypeSpec.Builder) {
        val typeR = TypeVariableName.invoke("R")
        type.addFunction(
            FunSpec.builder("process")
                .addTypeVariable(typeR)
                .returns(typeR)
                .addAnnotation(
                    AnnotationSpec.builder(Suppress::class)
                        .addMember("%S", "UNCHECKED_CAST")
                        .build()
                )
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("query", Framework.Query.parameterizedBy(typeR))
                .addCode(
                    CodeBlock.builder()
                        .add("val handler = this.resolve(query)\n")
                        .beginControlFlow("if (null !== handler)")
                        .add("return handler.execute(query = query, message = null) as R\n")
                        .endControlFlow()
                        .add("throw %T(query.toString())\n", Framework.QueryHandlerNotFoundException)
                        .build()
                )
                .build()
        )
    }

    internal fun buildGetVersioningStrategyFunction(type: TypeSpec.Builder) {
        type.addFunction(
            FunSpec.builder("getVersioningStrategy")
                .addModifiers(KModifier.OPEN)
                .addParameter("query", Framework.Query.parameterizedBy(TypeVariableName.invoke("*")))
                .returns(Framework.HandlerVersioningStrategy)
                .addStatement("return %T.useLatestVersion", Framework.HandlerVersioningStrategy)
                .build()
        )
    }

    internal fun groupHandlers(settings: List<QueryHandlerSetting>): Map<String, List<QueryHandlerSetting>> {
        val grouped = mutableMapOf<String, MutableList<QueryHandlerSetting>>()
        settings.forEach {
            val key = it.query.fullName()
            if (!grouped.containsKey(key)) {
                grouped[key] = mutableListOf()
            }
            grouped[key]!!.add(it)
        }
        return grouped
    }

    internal fun buildResolveFunction(settings: List<QueryHandlerSetting>, type: TypeSpec.Builder) {
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
                code.add("is %T -> ", first.query.toClassName())
                buildCodeToResolveHandler(first, code, type)
                code.add("\n")
            } else {
                code.beginControlFlow("is %T ->", it.value.first().query.toClassName())
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
                .addParameter("instance", Framework.Query.parameterizedBy(TypeVariableName.invoke("*")))
                .returns(
                    Framework.QueryHandler.parameterizedBy(
                        TypeVariableName.invoke("*"),
                        TypeVariableName.invoke("*")
                    ).copy(nullable = true)
                )
                .addCode(code.build())
                .build()
        )
    }

    internal fun buildCodeToResolveHandler(
        item: QueryHandlerSetting,
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
        settings: List<QueryHandlerSetting>,
        code: CodeBlock.Builder,
        type: TypeSpec.Builder
    ) {
        val map = mutableMapOf<Int, QueryHandlerSetting>()
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

    internal fun findFactoryFunctionName(setting: QueryHandlerSetting): Pair<Boolean, String> {
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