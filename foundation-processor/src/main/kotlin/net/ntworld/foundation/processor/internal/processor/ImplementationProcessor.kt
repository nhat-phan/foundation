package net.ntworld.foundation.processor.internal.processor

import net.ntworld.foundation.*
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryResult
import net.ntworld.foundation.cqrs.ReceivedData
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.ImplementationSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.processor.util.CodeUtility
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.util.FrameworkProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

class ImplementationProcessor : Processor {
    override val annotations: List<Class<out Annotation>> = listOf(
        Implementation::class.java
    )

    private val definedInterfaces = mapOf<String, ImplementationSetting.Type>(
        Aggregate::class.java.canonicalName to ImplementationSetting.Type.Aggregate,
        Error::class.java.canonicalName to ImplementationSetting.Type.Error,
        State::class.java.canonicalName to ImplementationSetting.Type.State,
        ReceivedData::class.java.canonicalName to ImplementationSetting.Type.ReceivedData,
        Event::class.java.canonicalName to ImplementationSetting.Type.Event,
        Command::class.java.canonicalName to ImplementationSetting.Type.Command,
        Query::class.java.canonicalName to ImplementationSetting.Type.Query,
        QueryResult::class.java.canonicalName to ImplementationSetting.Type.QueryResult,
        Request::class.java.canonicalName to ImplementationSetting.Type.Request,
        Response::class.java.canonicalName to ImplementationSetting.Type.Response,
        FrameworkProcessor.Contract to ImplementationSetting.Type.Unknown
    )

    private data class CollectedImplementation(
        val implementationPackageName: String,
        val implementationClassName: String,
        val contractPackageName: String,
        val contractClassName: String,
        val type: ImplementationSetting.Type,
        val isGenerated: Boolean
    )

    private val data: MutableMap<String, CollectedImplementation> = mutableMapOf()

    override fun startProcess(settings: GeneratorSettings) {
        data.clear()
        settings.implementations.forEach { item ->
            data[item.name] = CollectedImplementation(
                implementationPackageName = item.implementation.packageName,
                implementationClassName = item.implementation.className,
                contractPackageName = item.contract.packageName,
                contractClassName = item.contract.className,
                type = item.type,
                isGenerated = item.isGenerated
            )
        }
    }

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        val implementations = data.values
            .filter {
                it.contractClassName.isNotEmpty() &&
                    it.contractPackageName.isNotEmpty() &&
                    it.implementationClassName.isNotEmpty() &&
                    it.implementationPackageName.isNotEmpty()
            }
            .map {
                ImplementationSetting(
                    implementation = ClassInfo(
                        packageName = it.implementationPackageName,
                        className = it.implementationClassName
                    ),
                    contract = ClassInfo(
                        packageName = it.contractPackageName,
                        className = it.contractClassName
                    ),
                    isGenerated = it.isGenerated,
                    type = it.type
                )
            }

        return settings.copy(implementations = implementations)
    }

    override fun shouldProcess(
        annotation: Class<out Annotation>,
        element: Element,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ): Boolean {
        return when (annotation) {
            Implementation::class.java -> {
                element.kind.isClass && CodeUtility.isImplementInterface(
                    processingEnv,
                    element.asType(),
                    FrameworkProcessor.Contract,
                    true
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
        when (annotation) {
            Implementation::class.java -> {
                processElementsAnnotatedByImplementation(elements, processingEnv)
            }
        }
    }

    private fun processElementsAnnotatedByImplementation(
        elements: List<Element>,
        processingEnv: ProcessingEnvironment
    ) = elements.forEach { element ->
        val packageElement = processingEnv.elementUtils.getPackageOf(element)
        val className = element.simpleName.toString()
        val key = "${packageElement.qualifiedName}.$className"
        initImplementationSettingIfNeeded(packageElement.qualifiedName.toString(), className)

        val typeElement = element as? TypeElement ?: return@forEach
        findImplementedInterface(processingEnv, key, typeElement)
    }

    private fun findImplementedInterface(processingEnv: ProcessingEnvironment, key: String, element: TypeElement) {
        element.interfaces.forEach { type ->
            if (type !is DeclaredType) {
                return@forEach
            }

            val packageElement = processingEnv.elementUtils.getPackageOf(type.asElement())
            val packageName = packageElement.qualifiedName.toString()
            val className = type.asElement().simpleName.toString()

            definedInterfaces.forEach { (definedInterface, implType) ->
                if (CodeUtility.isImplementInterface(processingEnv, type, definedInterface, true)) {
                    ContractCollector.collect(processingEnv, type.asElement())
                    data[key] = data[key]!!.copy(
                        contractPackageName = packageName,
                        contractClassName = className,
                        type = implType
                    )
                    return@findImplementedInterface
                }
            }
        }
    }

    private fun initImplementationSettingIfNeeded(packageName: String, className: String) {
        val key = "$packageName.$className"
        if (!data.containsKey(key)) {
            data[key] = CollectedImplementation(
                implementationPackageName = packageName,
                implementationClassName = className,
                contractPackageName = "",
                contractClassName = "",
                isGenerated = false,
                type = ImplementationSetting.Type.Unknown
            )
        }
    }
}