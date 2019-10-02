package net.ntworld.foundation.processor.internal.processor

import net.ntworld.foundation.Handler
import net.ntworld.foundation.RequestHandler
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.RequestHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import net.ntworld.foundation.processor.internal.FoundationProcessorException
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
        val responsePackageName: String,
        val responseClassName: String,
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
                responsePackageName = item.response.packageName,
                responseClassName = item.response.className,
                handlerPackageName = item.handler.packageName,
                handlerClassName = item.handler.className,
                metadata = item.metadata,
                makeByFactory = item.makeByFactory,
                version = item.version
            )
        }
    }

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        return settings.copy(requestHandlers = data.values
            .filter {
                it.requestPackageName.isNotEmpty() &&
                    it.requestClassName.isNotEmpty() &&
                    it.responsePackageName.isNotEmpty() &&
                    it.responseClassName.isNotEmpty() &&
                    it.handlerPackageName.isNotEmpty() &&
                    it.handlerClassName.isNotEmpty()
            }
            .map {
                RequestHandlerSetting(
                    request = ClassInfo(
                        packageName = it.requestPackageName,
                        className = it.requestClassName
                    ),
                    response = ClassInfo(
                        packageName = it.responsePackageName,
                        className = it.responseClassName
                    ),
                    version = it.version,
                    handler = ClassInfo(
                        packageName = it.handlerPackageName,
                        className = it.handlerClassName
                    ),
                    metadata = it.metadata,
                    makeByFactory = it.makeByFactory
                )
            })
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
    ) = elements.forEach { element ->
        val packageName = this.getPackageNameOfClass(element)
        val className = element.simpleName.toString()
        val key = "$packageName.$className"
        initCollectedRequestHandlerIfNeeded(element, packageName, className)

        val implementedInterface = (element as TypeElement).interfaces
            .firstOrNull {
                val e = processingEnv.typeUtils.asElement(it) as? TypeElement ?: return@firstOrNull false
                e.qualifiedName.toString() == RequestHandler::class.java.canonicalName
            }

        if (null !== implementedInterface) {
            findImplementedRequestResponse(processingEnv, key, implementedInterface as DeclaredType)
        }

        data[key] = data[key]!!.copy(
            version = element.getAnnotation(Handler::class.java).version
        )
    }

    private fun findImplementedRequestResponse(processingEnv: ProcessingEnvironment, key: String, type: DeclaredType) {
        if (type.typeArguments.size != 2) {
            return
        }
        val requestType = type.typeArguments.first()
        val requestElement = processingEnv.typeUtils.asElement(requestType)
        ContractCollector.collect(processingEnv, requestElement)

        val responseType = type.typeArguments.last()
        val responseElement = processingEnv.typeUtils.asElement(responseType)
        ContractCollector.collect(processingEnv, responseElement)
        data[key] = data[key]!!.copy(
            requestPackageName = getPackageNameOfClass(requestElement),
            requestClassName = requestElement.simpleName.toString(),

            responsePackageName = getPackageNameOfClass(responseElement),
            responseClassName = responseElement.simpleName.toString()
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
                responsePackageName = "",
                responseClassName = "",
                version = 0,
                handlerPackageName = packageName,
                handlerClassName = className,
                metadata = KotlinMetadata.fromElement(element),
                makeByFactory = false
            )
        }
    }
}