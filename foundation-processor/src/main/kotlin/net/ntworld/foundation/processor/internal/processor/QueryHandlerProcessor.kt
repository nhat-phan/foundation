package net.ntworld.foundation.processor.internal.processor

import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.QueryHandler
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.QueryHandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import net.ntworld.foundation.processor.util.CodeUtility
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.internal.FoundationProcessorException
import net.ntworld.foundation.processor.util.FrameworkProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

internal class QueryHandlerProcessor() : Processor {
    override val annotations: List<Class<out Annotation>> = listOf(
        Handler::class.java
    )

    private data class CollectedQueryHandler(
        val queryPackageName: String,
        val queryClassName: String,
        val queryResultPackageName: String,
        val queryResultClassName: String,
        val handlerPackageName: String,
        val handlerClassName: String,
        val metadata: KotlinMetadata,
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
                queryResultPackageName = item.queryResult.packageName,
                queryResultClassName = item.queryResult.className,
                handlerPackageName = item.handler.packageName,
                handlerClassName = item.handler.className,
                metadata = item.metadata,
                makeByFactory = item.makeByFactory,
                version = item.version
            )
        }
    }

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        val queryHandlers = data.values
            .filter {
                it.queryPackageName.isNotEmpty() &&
                    it.queryClassName.isNotEmpty() &&
                    it.queryResultPackageName.isNotEmpty() &&
                    it.queryResultClassName.isNotEmpty() &&
                    it.handlerPackageName.isNotEmpty() &&
                    it.handlerClassName.isNotEmpty()
            }
            .map {
                QueryHandlerSetting(
                    query = ClassInfo(
                        packageName = it.queryPackageName,
                        className = it.queryClassName
                    ),
                    queryResult = ClassInfo(
                        packageName = it.queryResultPackageName,
                        className = it.queryResultClassName
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
    ) = elements.forEach { element ->
        val packageName = this.getPackageNameOfClass(element)
        val className = element.simpleName.toString()
        val key = "$packageName.$className"
        initCollectedQueryHandlerIfNeeded(element, packageName, className)

        val implementedInterface = (element as TypeElement).interfaces
            .firstOrNull {
                val e = processingEnv.typeUtils.asElement(it) as? TypeElement ?: return@firstOrNull false
                e.qualifiedName.toString() == QueryHandler::class.java.canonicalName
            }

        if (null !== implementedInterface) {
            findImplementedQueryAndResult(processingEnv, key, implementedInterface as DeclaredType)
        }

        data[key] = data[key]!!.copy(
            version = element.getAnnotation(Handler::class.java).version
        )
    }

    private fun findImplementedQueryAndResult(processingEnv: ProcessingEnvironment, key: String, type: DeclaredType) {
        if (type.typeArguments.size != 2) {
            return
        }
        val queryType = type.typeArguments.first()
        val queryElement = processingEnv.typeUtils.asElement(queryType)
        ContractCollector.collect(processingEnv, queryElement)

        val queryResultType = type.typeArguments.last()
        val queryResultElement = processingEnv.typeUtils.asElement(queryResultType)
        ContractCollector.collect(processingEnv, queryResultElement)

        data[key] = data[key]!!.copy(
            queryPackageName = getPackageNameOfClass(queryElement),
            queryClassName = queryElement.simpleName.toString(),
            queryResultPackageName = getPackageNameOfClass(queryResultElement),
            queryResultClassName = queryResultElement.simpleName.toString()
        )
    }

    private fun getPackageNameOfClass(element: Element): String {
        val upperElement = element.enclosingElement as? PackageElement ?: throw FoundationProcessorException(
            "@Handler do not support nested class."
        )

        return upperElement.qualifiedName.toString()
    }

    private fun initCollectedQueryHandlerIfNeeded(element: Element, packageName: String, className: String) {
        val key = "$packageName.$className"
        if (!data.containsKey(key)) {
            data[key] = CollectedQueryHandler(
                queryPackageName = "",
                queryClassName = "",
                queryResultPackageName = "",
                queryResultClassName = "",
                version = 0,
                handlerPackageName = packageName,
                handlerClassName = className,
                metadata = KotlinMetadata.fromElement(element),
                makeByFactory = false
            )
        }
    }
}