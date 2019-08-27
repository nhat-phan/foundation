package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.setting.CommandHandlerSettings
import net.ntworld.foundation.generator.type.ClassInfo

class LocalCommandBusGenerator {
    private val factoryFnMap = mutableMapOf<CommandHandlerSettings, String>()
    private val factoryFnNames = mutableListOf<String>()

    fun generate(settings: List<CommandHandlerSettings>): GeneratedFile {
        val target = Utility.findLocalCommandBusTarget(settings)
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    internal fun buildFile(settings: List<CommandHandlerSettings>, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(settings, target))

        return file.build()
    }

    internal fun buildClass(settings: List<CommandHandlerSettings>, target: ClassInfo): TypeSpec {
        val type = TypeSpec.classBuilder(target.className)
            .addSuperinterface(Framework.CommandBus)
            .addSuperinterface(
                Framework.LocalBusResolver.parameterizedBy(
                    Framework.Command,
                    Framework.CommandHandler.parameterizedBy(Framework.Command)
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
        type.addFunction(
            FunSpec.builder("process")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("command", Framework.Command)
                .addParameter("message", Framework.Message.copy(nullable = true))
                .addCode(
                    CodeBlock.builder()
                        .add("val handler = this.resolve(command)\n")
                        .beginControlFlow("if (null !== handler)")
                        .add("handler.handle(command = command, message = message)\n")
                        .endControlFlow()
                        .build()
                )
                .build()
        )
    }

    internal fun buildGetVersioningStrategyFunction(type: TypeSpec.Builder) {
        type.addFunction(
            FunSpec.builder("getVersioningStrategy")
                .addModifiers(KModifier.OPEN)
                .addParameter("command", Framework.Command)
                .returns(Framework.HandlerVersioningStrategy)
                .addStatement("return %T.useLatestVersion", Framework.HandlerVersioningStrategy)
                .build()
        )
    }

    internal fun groupHandlers(settings: List<CommandHandlerSettings>): Map<String, List<CommandHandlerSettings>> {
        val grouped = mutableMapOf<String, MutableList<CommandHandlerSettings>>()
        settings.forEach {
            val key = it.command.fullName()
            if (!grouped.containsKey(key)) {
                grouped[key] = mutableListOf()
            }
            grouped[key]!!.add(it)
        }
        return grouped
    }

    internal fun buildResolveFunction(settings: List<CommandHandlerSettings>, type: TypeSpec.Builder) {
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
                code.add("is %T -> ", first.command.toClassName())
                buildCodeToResolveHandler(first, code, type)
                code.add("\n")
            } else {
                code.beginControlFlow("is %T ->", it.value.first().command.toClassName())
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
                .addParameter("instance", Framework.Command)
                .returns(Framework.CommandHandler.parameterizedBy(Framework.Command).copy(nullable = true))
                .addCode(code.build())
                .build()
        )
    }

    internal fun buildCodeToResolveHandler(
        item: CommandHandlerSettings,
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
        settings: List<CommandHandlerSettings>,
        code: CodeBlock.Builder,
        type: TypeSpec.Builder
    ) {
        val map = mutableMapOf<Int, CommandHandlerSettings>()
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

    internal fun findFactoryFunctionName(settings: CommandHandlerSettings): Pair<Boolean, String> {
        if (factoryFnMap.contains(settings)) {
            return Pair(true, factoryFnMap[settings]!!)
        }

        val simpleName = settings.handler.className
        if (!factoryFnNames.contains(simpleName)) {
            factoryFnMap[settings] = simpleName
            factoryFnNames.add(simpleName)
            return Pair(false, simpleName)
        }
        factoryFnMap[settings] = "_${settings.handler.packageName.replace(".", "_")}_$simpleName"
        return Pair(false, factoryFnMap[settings]!!)
    }
}