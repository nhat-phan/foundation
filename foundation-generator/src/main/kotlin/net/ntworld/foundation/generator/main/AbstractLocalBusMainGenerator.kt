package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorOutput
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.HandlerSetting
import net.ntworld.foundation.generator.setting.VersionedHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.util.ConstructorComposer
import net.ntworld.foundation.generator.util.HandlerReader

abstract class AbstractLocalBusMainGenerator<T : HandlerSetting> {
    private val constructorComposer = ConstructorComposer()
    private val factoryFnMap = mutableMapOf<T, String>()
    private val factoryFnNames = mutableListOf<String>()
    protected var isAbstract: Boolean = false
        private set

    protected abstract fun findTarget(settings: List<T>, namespace: String?): ClassInfo

    protected abstract fun buildClass(settings: List<T>, target: ClassInfo): TypeSpec.Builder

    fun generate(settings: List<T>, namespace: String? = null): GeneratedFile {
        val target = findTarget(settings, namespace)
        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildMainGeneratedFile(target, stringBuffer.toString())
    }

    private fun buildFile(settings: List<T>, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)

        val type = buildClass(settings, target)
        constructorComposer.generateComposedConstructor(type)

        file.addType(type.build())
        return file.build()
    }

    protected fun makeGetVersioningStrategyFunctionBuilder(): FunSpec.Builder {
        return FunSpec.builder("getVersioningStrategy")
            .addModifiers(KModifier.OPEN)
            .returns(Framework.HandlerVersioningStrategy)
            .addStatement(
                "return %T.useLatestVersion",
                Framework.HandlerVersioningStrategy
            )
    }

    protected fun makeStrategyDefinitionCodeBlock(): CodeBlock {
        val code = CodeBlock.Builder()
        code.add("val strategy = getVersioningStrategy(instance)\n")
            .beginControlFlow("if (strategy.skip())")
            .add("return null\n")
            .endControlFlow()
            .add("\n")
        return code.build()
    }

    protected fun makeResolveCodeBlock(name: String, returnFn: (CodeBlock.Builder) -> Unit): CodeBlock {
        val code = CodeBlock.builder()
            .add("val handler = this.resolve(%L)\n", name)
            .beginControlFlow("if (null !== handler)")

        returnFn.invoke(code)
        code.endControlFlow()
        return code.build()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <V : VersionedHandlerSetting> buildCodeToResolveVersionedHandler(
        settings: List<V>,
        code: CodeBlock.Builder,
        type: TypeSpec.Builder
    ) {
        val map = mutableMapOf<Int, V>()
        var latestVersion: Int = Int.MIN_VALUE
        for (item in settings) {
            map[item.version] = item

            if (latestVersion < item.version) {
                latestVersion = item.version
            }
        }

        code.beginControlFlow("if (strategy.useLatestVersion())")
        code.add("return ")
        buildCodeToResolveHandler(map[latestVersion]!! as T, code, type)
        code.add("\n")
        code.endControlFlow()
        code.add("\n")

        code.beginControlFlow("return when (strategy.specificVersion)")
        map.keys.sorted().forEach {
            code.add("%L -> ", map[it]!!.version)
            buildCodeToResolveHandler(map[it]!! as T, code, type)
            code.add("\n")
        }
        code.add("else -> null\n")
        code.endControlFlow()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <V : VersionedHandlerSetting> makeVersionedResolveHandlerCodeBlock(
        grouped: Map<String, List<V>>,
        type: TypeSpec.Builder,
        contractFn: (T) -> ClassName
    ): CodeBlock {
        val code = CodeBlock.builder()
        code.add(makeStrategyDefinitionCodeBlock())
        code.beginControlFlow("return when (instance)")

        grouped.forEach {
            if (it.value.size == 1) {
                val first = it.value.first()
                code.add("is %T -> ", contractFn(first as T))
                buildCodeToResolveHandler(first as T, code, type)
                code.add("\n")
            } else {
                code.beginControlFlow("is %T ->", contractFn(it.value.first() as T))
                buildCodeToResolveVersionedHandler(it.value, code, type)
                code.endControlFlow()
            }
            code.add("\n")
        }

        code.add("else -> null\n")
        code.endControlFlow()
        return code.build()
    }

    protected fun buildCodeToResolveHandler(
        item: T,
        code: CodeBlock.Builder,
        type: TypeSpec.Builder
    ) {
        val (exist, factoryFnName) = this.findFactoryFunctionName(item)
        if (!item.makeByFactory) {
            val constructor = HandlerReader.findPrimaryConstructor(item)
            if (null !== constructor) {
                constructorComposer.add(factoryFnName, constructor)
            }
            code.add("%T", item.handler.toClassName())
            code.add(constructorComposer.generateNewInstanceCodeBlockFor(factoryFnName))
            return
        }

        val fnName = "make$factoryFnName"
        if (!exist) {
            isAbstract = true
            type.addFunction(
                FunSpec.builder(fnName)
                    .addModifiers(KModifier.PROTECTED, KModifier.ABSTRACT)
                    .returns(item.handler.toClassName())
                    .build()
            )
        }
        code.add("%L()", fnName)
    }

    protected fun groupHandlers(settings: List<T>, fn: (T) -> String): Map<String, List<T>> {
        val grouped = mutableMapOf<String, MutableList<T>>()
        settings.forEach {
            val key = fn.invoke(it)
            if (!grouped.containsKey(key)) {
                grouped[key] = mutableListOf()
            }
            grouped[key]!!.add(it)
        }
        return grouped
    }

    private fun findFactoryFunctionName(setting: T): Pair<Boolean, String> {
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