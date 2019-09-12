package net.ntworld.foundation.processor.internal.processor

import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.CommandHandler
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.CommandHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import net.ntworld.foundation.processor.util.CodeUtility
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.FoundationProcessorException
import net.ntworld.foundation.processor.util.FrameworkProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

internal class CommandHandlerProcessor : Processor {
    override val annotations: List<Class<out Annotation>> = listOf(
        Handler::class.java
    )

    internal data class CollectedCommandHandler(
        val commandPackageName: String,
        val commandClassName: String,
        val handlerPackageName: String,
        val handlerClassName: String,
        val metadata: KotlinMetadata,
        val makeByFactory: Boolean,
        val version: Int
    )

    private val data = mutableMapOf<String, CollectedCommandHandler>()

    override fun startProcess(settings: GeneratorSettings) {
        data.clear()
        settings.commandHandlers.forEach { item ->
            data[item.name] = CollectedCommandHandler(
                commandPackageName = item.command.packageName,
                commandClassName = item.command.className,
                handlerPackageName = item.handler.packageName,
                handlerClassName = item.handler.className,
                metadata = item.metadata,
                makeByFactory = item.makeByFactory,
                version = item.version
            )
        }
    }

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        val commandHandlers = data.values.map {
            CommandHandlerSetting(
                command = ClassInfo(
                    packageName = it.commandPackageName,
                    className = it.commandClassName
                ),
                version = it.version,
                handler = ClassInfo(
                    packageName = it.handlerPackageName,
                    className = it.handlerClassName
                ),
                metadata = it.metadata,
                makeByFactory = it.makeByFactory
            )
        }
        return settings.copy(commandHandlers = commandHandlers)
    }

    override fun shouldProcess(
        annotation: Class<out Annotation>,
        element: Element,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ): Boolean {
        return when (annotation) {
            Handler::class.java -> {
                CodeUtility.isImplementInterface(
                    processingEnv, element.asType(), CommandHandler::class.java.canonicalName, false
                )
            }

            else -> false
        }
    }

    override fun process(
        annotation: Class<out Annotation>,
        elements: List<Element>,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ) = elements.forEach { element ->
        val packageName = this.getPackageNameOfClass(element)
        val className = element.simpleName.toString()
        val key = "$packageName.$className"
        initCollectedCommandHandlerIfNeeded(element, packageName, className)

        // If the Handler is provided enough information, then no need to find data
        if (processAnnotationProperties(processingEnv, key, element, element.getAnnotation(Handler::class.java))) {
            return@forEach
        }

        val implementedInterface = (element as TypeElement).interfaces
            .firstOrNull {
                val e = processingEnv.typeUtils.asElement(it) as? TypeElement ?: return@firstOrNull false
                e.qualifiedName.toString() == CommandHandler::class.java.canonicalName
            }

        if (null !== implementedInterface) {
            findImplementedCommand(processingEnv, key, implementedInterface as DeclaredType)
        }

        data[key] = data[key]!!.copy(
            version = element.getAnnotation(Handler::class.java).version
        )
    }

    private fun processAnnotationProperties(
        processingEnv: ProcessingEnvironment,
        key: String,
        element: Element,
        annotation: Handler
    ): Boolean {
        if (annotation.type != Handler.Type.Query) {
            return false
        }

        var inputTypeName = ""
        val mirrors = element.annotationMirrors
        mirrors.forEach {
            if (it.annotationType.toString() == FrameworkProcessor.Handler) {
                it.elementValues.forEach {
                    if (it.key.simpleName.toString() == "input" &&
                        it.value.value.toString() != java.lang.Object::class.java.canonicalName
                    ) {
                        inputTypeName = it.value.value.toString()
                    }
                }
            }
        }

        if (inputTypeName.isEmpty()) {
            return false
        }

        val inputElement = processingEnv.elementUtils.getTypeElement(inputTypeName)
        ContractCollector.collect(processingEnv, inputElement)
        data[key] = data[key]!!.copy(
            commandPackageName = getPackageNameOfClass(inputElement),
            commandClassName = inputElement.simpleName.toString(),
            version = annotation.version,
            makeByFactory = annotation.factory
        )
        return true
    }

    private fun findImplementedCommand(processingEnv: ProcessingEnvironment, key: String, type: DeclaredType) {
        if (type.typeArguments.size != 1) {
            return
        }
        val commandType = type.typeArguments.first()
        val element = processingEnv.typeUtils.asElement(commandType)
        ContractCollector.collect(processingEnv, element)
        data[key] = data[key]!!.copy(
            commandPackageName = getPackageNameOfClass(element),
            commandClassName = element.simpleName.toString()
        )
    }

    private fun getPackageNameOfClass(element: Element): String {
        val upperElement = element.enclosingElement as? PackageElement ?: throw FoundationProcessorException(
            "@Handler do not support nested class."
        )

        return upperElement.qualifiedName.toString()
    }

    private fun initCollectedCommandHandlerIfNeeded(element: Element, packageName: String, className: String) {
        val key = "$packageName.$className"
        if (!data.containsKey(key)) {
            data[key] = CollectedCommandHandler(
                commandPackageName = "",
                commandClassName = "",
                version = 0,
                handlerPackageName = packageName,
                handlerClassName = className,
                metadata = KotlinMetadata.fromElement(element),
                makeByFactory = false
            )
        }
    }
}