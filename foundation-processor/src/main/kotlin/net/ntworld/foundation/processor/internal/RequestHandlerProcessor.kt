package net.ntworld.foundation.processor.internal

import net.ntworld.foundation.Handler
import net.ntworld.foundation.RequestHandler
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.RequestHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import net.ntworld.foundation.processor.FoundationProcessorException
import net.ntworld.foundation.processor.util.CodeUtility
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.util.FrameworkProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

internal class RequestHandlerProcessor : Processor {
    override val annotations: List<Class<out Annotation>> = listOf(
        Handler::class.java
    )

    private data class CollectedRequestHandler(
        val requestPackageName: String,
        val requestClassName: String,
        val handlerPackageName: String,
        val handlerClassName: String,
        val metadata: KotlinMetadata,
        val makeByFactory: Boolean,
        val version: Int
    )

    private val data = mutableMapOf<String, CollectedRequestHandler>()

    override fun startProcess(settings: GeneratorSettings) {
        data.clear()
        settings.requestHandlers.forEach { item ->
            data[item.name] = CollectedRequestHandler(
                requestPackageName = item.request.packageName,
                requestClassName = item.request.className,
                handlerPackageName = item.handler.packageName,
                handlerClassName = item.handler.className,
                metadata = item.metadata,
                makeByFactory = item.makeByFactory,
                version = item.version
            )
        }
    }

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        val requestHandlers = data.values.map {
            RequestHandlerSetting(
                request = ClassInfo(
                    packageName = it.requestPackageName,
                    className = it.requestClassName
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
        return settings.copy(requestHandlers = requestHandlers)
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
                    processingEnv, element.asType(), RequestHandler::class.java.canonicalName, false
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
            initCollectedRequestHandlerIfNeeded(element, packageName, className)

            // If the Handler is provided enough information, then no need to find data
            if (processAnnotationProperties(processingEnv, key, element, element.getAnnotation(Handler::class.java))) {
                return@forEach
            }

            val implementedInterface = (element as TypeElement).interfaces
                .firstOrNull {
                    val e = processingEnv.typeUtils.asElement(it) as? TypeElement ?: return@firstOrNull false
                    e.qualifiedName.toString() == RequestHandler::class.java.canonicalName
                }

            if (null !== implementedInterface) {
                findImplementedRequest(processingEnv, key, implementedInterface as DeclaredType)
            }

            data[key] = data[key]!!.copy(
                version = element.getAnnotation(Handler::class.java).version,
                makeByFactory = !CodeUtility.canConstructedByInfrastructure(
                    processingEnv,
                    element
                )
            )
        }
    }

    private fun processAnnotationProperties(
        processingEnv: ProcessingEnvironment,
        key: String,
        element: Element,
        annotation: Handler
    ): Boolean {
        if (annotation.type !== Handler.Type.Request) {
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
            requestPackageName = getPackageNameOfClass(inputElement),
            requestClassName = inputElement.simpleName.toString(),
            version = annotation.version,
            makeByFactory = annotation.factory
        )
        return true
    }

    private fun findImplementedRequest(processingEnv: ProcessingEnvironment, key: String, type: DeclaredType) {
        if (type.typeArguments.size != 2) {
            return
        }
        val requestType = type.typeArguments.first()
        val element = processingEnv.typeUtils.asElement(requestType)
        ContractCollector.collect(processingEnv, element)
        data[key] = data[key]!!.copy(
            requestPackageName = getPackageNameOfClass(element),
            requestClassName = element.simpleName.toString()
        )
    }

    private fun getPackageNameOfClass(element: Element): String {
        val upperElement = element.enclosingElement as? PackageElement ?: throw FoundationProcessorException(
            "@Handler do not support nested class."
        )

        return upperElement.qualifiedName.toString()
    }

    private fun initCollectedRequestHandlerIfNeeded(element: Element, packageName: String, className: String) {
        val key = "$packageName.$className"
        if (!data.containsKey(key)) {
            data[key] = CollectedRequestHandler(
                requestPackageName = "",
                requestClassName = "",
                version = 0,
                handlerPackageName = packageName,
                handlerClassName = className,
                metadata = KotlinMetadata.fromElement(element),
                makeByFactory = false
            )
        }
    }
}