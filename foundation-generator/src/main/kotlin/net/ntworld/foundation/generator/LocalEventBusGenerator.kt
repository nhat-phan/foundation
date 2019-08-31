package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.setting.EventHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo

class LocalEventBusGenerator {
    private val factoryFnMap = mutableMapOf<EventHandlerSetting, String>()
    private val factoryFnNames = mutableListOf<String>()

    fun generate(settings: List<EventHandlerSetting>): GeneratedFile {
        val target = Utility.findLocalEventBusTarget(settings)
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    internal fun buildFile(settings: List<EventHandlerSetting>, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(settings, target))

        return file.build()
    }

    internal fun buildClass(settings: List<EventHandlerSetting>, target: ClassInfo): TypeSpec {
        val type = TypeSpec.classBuilder(target.className)
            .addSuperinterface(Framework.EventBus)
            .addSuperinterface(
                Framework.LocalBusResolver.parameterizedBy(
                    Framework.Event,
                    getHandlersArrayTypeName()
                )
            )

        buildConstructor(type)
        buildPublishFunction(type)
        buildProcessFunction(type)
        buildResolveFunction(settings, type)
        if (factoryFnNames.isNotEmpty()) {
            type.addModifiers(KModifier.ABSTRACT)
        }
        return type.build()
    }

    internal fun getHandlersArrayTypeName(): TypeName {
        return ClassName(
            "kotlin",
            "Array"
        ).parameterizedBy(
            Framework.EventHandler.parameterizedBy(Framework.Event)
        )
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
                .addParameter("message", Framework.Message.copy(nullable = true))
                .addCode(
                    CodeBlock.builder()
                        .add("val handlers = this.resolve(event)\n")
                        .beginControlFlow("if (null !== handlers)")
                        .add("handlers.forEach { it.handle(event = event, message = message) }\n")
                        .endControlFlow()
                        .build()
                )
                .build()
        )
    }

    internal fun groupHandlers(settings: List<EventHandlerSetting>): Map<ClassInfo, List<EventHandlerSetting>> {
        val grouped = mutableMapOf<ClassInfo, MutableList<EventHandlerSetting>>()
        val classInfoMap = mutableMapOf<String, ClassInfo>()
        settings.forEach { setting ->
            setting.events.forEach {
                val key = it.fullName()
                if (!classInfoMap.containsKey(key)) {
                    classInfoMap[key] = it.copy()
                }

                if (!grouped.containsKey(classInfoMap[key]!!)) {
                    grouped[classInfoMap[key]!!] = mutableListOf()
                }
                grouped[classInfoMap[key]!!]!!.add(setting)
            }
        }
        return grouped
    }

    internal fun buildResolveFunction(settings: List<EventHandlerSetting>, type: TypeSpec.Builder) {
        val grouped = groupHandlers(settings)

        val code = CodeBlock.builder()
        code.beginControlFlow("return when (instance)")

        grouped.forEach {
            code.add("is %T -> arrayOf(\n", it.key.toClassName())
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

    internal fun buildCodeToResolveHandler(
        item: EventHandlerSetting,
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

    internal fun findFactoryFunctionName(setting: EventHandlerSetting): Pair<Boolean, String> {
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