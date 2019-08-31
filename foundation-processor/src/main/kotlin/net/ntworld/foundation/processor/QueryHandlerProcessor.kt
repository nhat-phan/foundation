package net.ntworld.foundation.processor

import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.QueryHandler
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.QueryHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

class QueryHandlerProcessor() : Processor {
    override val annotations: List<Class<out Annotation>> = listOf(
        Handler::class.java
    )

    internal data class CollectedQueryHandler(
        val queryPackageName: String,
        val queryClassName: String,
        val handlerPackageName: String,
        val handlerClassName: String,
        val makeByFactory: Boolean,
        val version: Int
    )

    private val data = mutableMapOf<String, CollectedQueryHandler>()

    override fun startProcess(settings: GeneratorSettings) {
        data.clear()
        settings.queryHandlers.forEach { item ->
            data[item.name] = CollectedQueryHandler(
                queryPackageName = item.query.packageName,
                queryClassName = item.query.className,
                handlerPackageName = item.handler.packageName,
                handlerClassName = item.handler.className,
                makeByFactory = item.makeByFactory,
                version = item.version
            )
        }
    }

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        val queryHandlers = data.values.map {
            QueryHandlerSetting(
                query = ClassInfo(
                    packageName = it.queryPackageName,
                    className = it.queryClassName
                ),
                version = it.version,
                handler = ClassInfo(
                    packageName = it.handlerPackageName,
                    className = it.handlerClassName
                ),
                makeByFactory = it.makeByFactory
            )
        }
        return settings.copy(queryHandlers = queryHandlers)
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
                    processingEnv, element.asType(), QueryHandler::class.java.canonicalName, false
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
            initCollectedQueryHandlerIfNeeded(packageName, className)

            // If the Handler is provided enough information, then no need to find data
            if (processAnnotationProperties(processingEnv, key, element, element.getAnnotation(Handler::class.java))) {
                return@forEach
            }

            val implementedInterface = (element as TypeElement).interfaces
                .firstOrNull {
                    val e = processingEnv.typeUtils.asElement(it) as? TypeElement ?: return@firstOrNull false
                    e.qualifiedName.toString() == QueryHandler::class.java.canonicalName
                }

            if (null !== implementedInterface) {
                findImplementedQuery(processingEnv, key, implementedInterface as DeclaredType)
            }

            data[key] = data[key]!!.copy(
                version = element.getAnnotation(Handler::class.java).version,
                makeByFactory = !CodeUtility.canConstructedByInfrastructure(processingEnv, element)
            )
        }
    }

    private fun processAnnotationProperties(
        processingEnv: ProcessingEnvironment,
        key: String,
        element: Element,
        annotation: Handler
    ): Boolean {
        if (annotation.type !== Handler.Type.Query) {
            return false
        }

        var inputTypeName = ""
        val mirrors = element.annotationMirrors
        mirrors.forEach {
            if (it.annotationType.toString() == FrameworkProcessor.Handler) {
                it.elementValues.forEach {
                    if (it.key.simpleName.toString() == "input" &&
                        it.value.value.toString() !== java.lang.Object::class.java.canonicalName
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
        data[key] = data[key]!!.copy(
            queryPackageName = getPackageNameOfClass(inputElement),
            queryClassName = inputElement.simpleName.toString(),
            version = annotation.version,
            makeByFactory = annotation.factory
        )
        return true
    }

    private fun findImplementedQuery(processingEnv: ProcessingEnvironment, key: String, type: DeclaredType) {
        if (type.typeArguments.size != 2) {
            return
        }
        val queryType = type.typeArguments.first()
        val element = processingEnv.typeUtils.asElement(queryType)
        data[key] = data[key]!!.copy(
            queryPackageName = getPackageNameOfClass(element),
            queryClassName = element.simpleName.toString()
        )
    }

    private fun getPackageNameOfClass(element: Element): String {
        val upperElement = element.enclosingElement as? PackageElement ?: throw FoundationProcessorException(
            "@Handler do not support nested class."
        )

        return upperElement.qualifiedName.toString()
    }

    private fun initCollectedQueryHandlerIfNeeded(packageName: String, className: String) {
        val key = "$packageName.$className"
        if (!data.containsKey(key)) {
            data[key] = CollectedQueryHandler(
                queryPackageName = "",
                queryClassName = "",
                version = 0,
                handlerPackageName = packageName,
                handlerClassName = className,
                makeByFactory = false
            )
        }
    }
}