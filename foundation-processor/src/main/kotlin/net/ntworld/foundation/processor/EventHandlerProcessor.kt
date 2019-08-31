package net.ntworld.foundation.processor

import net.ntworld.foundation.EventHandler
import net.ntworld.foundation.Handler
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.EventHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

class EventHandlerProcessor() : Processor {
    override val annotations: List<Class<out Annotation>> = listOf(
        Handler::class.java
    )

    internal data class CollectedEvent(
        val packageName: String,
        val className: String
    )

    internal data class CollectedEventHandler(
        val events: MutableList<CollectedEvent>,
        val handlerPackageName: String,
        val handlerClassName: String,
        val makeByFactory: Boolean
    )

    private val data = mutableMapOf<String, CollectedEventHandler>()

    override fun startProcess(settings: GeneratorSettings) {
        data.clear()
        settings.eventHandlers.forEach { item ->
            data[item.name] = CollectedEventHandler(
                events = item.events.map {
                    CollectedEvent(it.packageName, it.className)
                }.toMutableList(),
                handlerPackageName = item.handler.packageName,
                handlerClassName = item.handler.className,
                makeByFactory = item.makeByFactory
            )
        }
    }

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        val eventHandlers = data.values.map {
            EventHandlerSetting(
                events = it.events.map {
                    ClassInfo(
                        packageName = it.packageName,
                        className = it.className
                    )
                },
                handler = ClassInfo(
                    packageName = it.handlerPackageName,
                    className = it.handlerClassName
                ),
                makeByFactory = it.makeByFactory
            )
        }
        return settings.copy(eventHandlers = eventHandlers)
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
                    processingEnv, element.asType(), EventHandler::class.java.canonicalName, false
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
    ) {
        elements.forEach { element ->
            val packageName = this.getPackageNameOfClass(element)
            val className = element.simpleName.toString()
            val key = "$packageName.$className"
            initCollectedEventHandlerIfNeeded(packageName, className)

            (element as TypeElement).interfaces
                .filter {
                    val e = processingEnv.typeUtils.asElement(it) as? TypeElement ?: return@filter false
                    e.qualifiedName.toString() == EventHandler::class.java.canonicalName
                }
                .forEach { findEvent(processingEnv, key, it as DeclaredType) }
        }
    }

    private fun findEvent(processingEnv: ProcessingEnvironment, key: String, type: DeclaredType) {
        if (type.typeArguments.size != 1) {
            return
        }
        val eventType = type.typeArguments.first()
        val element = processingEnv.typeUtils.asElement(eventType)
        data[key]!!.events.add(
            CollectedEvent(
                packageName = getPackageNameOfClass(element),
                className = element.simpleName.toString()
            )
        )
    }

    private fun getPackageNameOfClass(element: Element): String {
        val upperElement = element.enclosingElement as? PackageElement ?: throw FoundationProcessorException(
            "@Handler do not support nested class."
        )

        return upperElement.qualifiedName.toString()
    }

    private fun initCollectedEventHandlerIfNeeded(packageName: String, className: String) {
        val key = "$packageName.$className"
        if (!data.containsKey(key)) {
            data[key] = CollectedEventHandler(
                events = mutableListOf(),
                handlerPackageName = packageName,
                handlerClassName = className,
                makeByFactory = false
            )
        }
    }
}