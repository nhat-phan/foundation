package net.ntworld.foundation.generator.main

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.setting.EventSourcingSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.Constructor
import net.ntworld.foundation.generator.util.ConstructorComposer
import net.ntworld.foundation.generator.util.MultiPlatformCodeGenerator

class InfrastructureProviderMainGenerator {
    private data class KnownMessageTranslator(
        val contract: ClassInfo,

        val messageTranslator: ClassInfo
    )

    private val constructorComposer = ConstructorComposer()
    private val variableNames = mutableListOf<String>()
    private val localBusesStatus = mutableMapOf<String, Boolean>()
    private val messageTranslators = mutableMapOf<String, KnownMessageTranslator>()

    fun setLocalBusesStatus(busName: String, enabled: Boolean) {
        localBusesStatus[busName] = enabled
    }

    fun addToConstructorComposer(resolvableName: String, constructor: Constructor) {
        constructorComposer.add(resolvableName, constructor)
    }

    fun registerMessageTranslator(contract: ClassInfo, messageTranslator: ClassInfo) {
        messageTranslators[contract.fullName()] = KnownMessageTranslator(
            contract = contract,
            messageTranslator = messageTranslator
        )
    }

    fun generate(platform: Platform, settings: GeneratorSettings, namespace: String? = null): GeneratedFile {
        val target = Utility.findInfrastructureProviderTarget(settings, namespace)
        val file = buildFile(platform, settings, namespace, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return GeneratedFile.makeMainFile(target, file.toString())
    }

    private fun buildFile(platform: Platform, settings: GeneratorSettings, namespace: String?, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(platform, settings, namespace, target))

        return file.build()
    }

    private fun buildClass(platform: Platform, settings: GeneratorSettings, namespace: String?, target: ClassInfo): TypeSpec {
        val type = TypeSpec
            .classBuilder(target.toClassName())
            .addModifiers(KModifier.OPEN)
            .superclass(Framework.InfrastructureProvider)
        val init = CodeBlock.builder()

        init.add(buildRegisterMessageTranslatorCodeBlock())
        init.add("\n")

        buildEnvironment(platform, type)
        buildIdGenerator(platform, type)
        buildFaker(platform, type)

        buildLocalBusIfNeeded(
            type, "eventBus", Framework.EventBus, Utility.findLocalEventBusTarget(
                settings.eventHandlers, namespace
            )
        )

        buildLocalBusIfNeeded(
            type, "serviceBus", Framework.ServiceBus, Utility.findLocalServiceBusTarget(
                settings.requestHandlers, namespace
            )
        )

        buildLocalBusIfNeeded(
            type, "commandBus", Framework.CommandBus, Utility.findLocalCommandBusTarget(
                settings.commandHandlers, namespace
            )
        )

        buildLocalBusIfNeeded(
            type, "queryBus", Framework.QueryBus, Utility.findLocalQueryBusTarget(
                settings.queryHandlers, namespace
            )
        )

        constructorComposer.generateComposedConstructor(type)
        type.addInitializerBlock(init.build())
        return type.build()
    }

    private fun buildLocalBusIfNeeded(
        type: TypeSpec.Builder,
        funcName: String,
        returnsType: TypeName,
        busTarget: ClassInfo
    ) {
        val status = localBusesStatus[busTarget.className]
        if (null === status || !status) {
            return
        }

        val func = FunSpec.builder(funcName)
        func.addModifiers(KModifier.OVERRIDE)
            .returns(returnsType)
            .addCode("return %T", busTarget.toClassName())
            .addCode(constructorComposer.generateNewInstanceCodeBlockFor(busTarget.className))
            .addCode("\n")

        type.addFunction(func.build())
    }

    private fun buildFaker(platform: Platform, type: TypeSpec.Builder) {
        val code = when (platform) {
            Platform.Jvm -> MultiPlatformCodeGenerator.getInitDefaultFakerForJvm()
        }

        val func = FunSpec.builder("faker")
        func.addModifiers(KModifier.OVERRIDE)
            .returns(Framework.Faker)
            .addCode("return ")
            .addCode(code)
            .addCode("\n")

        type.addFunction(func.build())
    }

    private fun buildIdGenerator(platform: Platform, type: TypeSpec.Builder) {
        val code = when (platform) {
            Platform.Jvm -> MultiPlatformCodeGenerator.getInitIdGeneratorForJvm()
        }

        val func = FunSpec.builder("idGeneratorOf")
        func.addModifiers(KModifier.OVERRIDE)
            .returns(Framework.IdGenerator)
            .addCode("return ")
            .addCode(code)
            .addCode("\n")

        type.addFunction(func.build())
    }

    private fun buildEnvironment(platform: Platform, type: TypeSpec.Builder) {
        val getEnv = when (platform) {
            Platform.Jvm -> MultiPlatformCodeGenerator.getEnvironmentForJvm("ENVIRONMENT", "production")
        }
        val code = CodeBlock.builder()
        code.add("val env = ")
        code.add(getEnv)
        code.add("\n")

        code.beginControlFlow("return when (env.toLowerCase())")
            .add("%S -> %T.Development", "dev", Framework.Environment)
            .add("\n")
            .add("%S -> %T.Development", "development", Framework.Environment)
            .add("\n")
            .add("%S -> %T.Test", "test", Framework.Environment)
            .add("\n")
            .add("%S -> %T.Test", "testing", Framework.Environment)
            .add("\n")
            .add("%S -> %T.Stage", "stage", Framework.Environment)
            .add("\n")
            .add("%S -> %T.Stage", "staging", Framework.Environment)
            .add("\n")
            .add("%S -> %T.Production", "prod", Framework.Environment)
            .add("\n")
            .add("%S -> %T.Production", "production", Framework.Environment)
            .add("\n")
            .add("else -> %T.Production", Framework.Environment)
            .add("\n")
            .endControlFlow()

        val func = FunSpec.builder("environment")
        func.addModifiers(KModifier.OVERRIDE)
            .returns(Framework.Environment)
            .addCode(code.build())

        type.addFunction(func.build())
    }

    private fun buildRegisterMessageTranslatorCodeBlock(): CodeBlock {
        val code = CodeBlock.builder()
        messageTranslators.values.forEach {
            code.add(
                "registerMessageTranslator(%T::class, %T)\n",
                it.contract.toClassName(),
                it.messageTranslator.toClassName()
            )
        }
        return code.build()
    }

    // TODO: Check when working with EventSourcing
    private fun buildRegisterCodeForEvent(
        type: TypeSpec.Builder,
        init: CodeBlock.Builder,
        setting: EventSourcingSetting
    ) {
        val eventConverter = Utility.findEventConverterTarget(setting)
        val eventConverterClass = ClassName(eventConverter.packageName, eventConverter.className)
        val eventClass = ClassName(setting.event.packageName, setting.event.className)

        val eventConverterVariableName = findVariableNames(eventConverter.packageName, eventConverter.className)
        type.addProperty(
            PropertySpec.builder(eventConverterVariableName, eventConverterClass)
                .addModifiers(KModifier.PRIVATE)
                .initializer(CodeBlock.of("%T(this)", eventConverterClass))
                .build()
        )

        init.indent()
        init.add(
            "registerEventConverter(%T::class, this.%L)\n",
            eventClass,
            eventConverterVariableName
        )
        init.add(
            "registerEventConverter(%S, %L, %L)\n",
            setting.type,
            setting.variant,
            eventConverterVariableName
        )
        init.add("\n")
        init.unindent()
    }

    private fun findVariableNames(packageName: String, className: String): String {
        val simpleName = className.decapitalize()
        if (!this.variableNames.contains(simpleName)) {
            this.variableNames.add(simpleName)
            return simpleName
        }

        val complexName = packageName.replace(".", "_") + "_" + simpleName
        this.variableNames.add(complexName)
        return complexName
    }

    companion object {
        fun findTarget(settings: GeneratorSettings): ClassInfo {
            return Utility.findInfrastructureProviderTarget(settings)
        }
    }
}