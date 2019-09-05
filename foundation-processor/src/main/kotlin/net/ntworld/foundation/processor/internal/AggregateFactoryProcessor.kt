package net.ntworld.foundation.processor.internal

import net.ntworld.foundation.*
import net.ntworld.foundation.eventSourcing.EventSourced
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.AggregateFactorySetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.processor.util.CodeUtility
import net.ntworld.foundation.processor.util.ContractCollector
import net.ntworld.foundation.processor.FoundationProcessorException
import net.ntworld.foundation.processor.util.FrameworkProcessor
import javax.annotation.processing.*
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

internal class AggregateFactoryProcessor : Processor {
    internal data class CollectedFactory(
        var aggregatePackageName: String,
        var aggregateClassName: String,
        var statePackageName: String,
        var stateClassName: String,
        var implementationPackageName: String,
        var implementationClassName: String,
        var isAbstract: Boolean,
        var isEventSourced: Boolean,
        var extendsAbstractEventSourced: Boolean
    )

    override val annotations: List<Class<out Annotation>> = listOf(
        Implementation::class.java,
        EventSourced::class.java
    )

    private val data: MutableMap<String, CollectedFactory> = mutableMapOf()

    override fun startProcess(settings: GeneratorSettings) {
        data.clear()
        settings.aggregateFactories.forEach { item ->
            data[item.name] = CollectedFactory(
                aggregatePackageName = item.aggregate.packageName,
                aggregateClassName = item.aggregate.className,
                implementationPackageName = item.implementation.packageName,
                implementationClassName = item.implementation.className,
                statePackageName = item.state.packageName,
                stateClassName = item.state.className,
                isAbstract = item.isAbstract,
                isEventSourced = item.isEventSourced,
                extendsAbstractEventSourced = true
            )
        }
    }

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        val factories = data.values
            .filter {
                if (it.isEventSourced && !it.extendsAbstractEventSourced) {
                    return@filter false
                }
                (it.isAbstract || it.isEventSourced) &&
                    it.aggregatePackageName.isNotEmpty() &&
                    it.aggregateClassName.isNotEmpty() &&
                    it.statePackageName.isNotEmpty() &&
                    it.stateClassName.isNotEmpty() &&
                    it.implementationPackageName.isNotEmpty() &&
                    it.implementationClassName.isNotEmpty()
            }
            .map {
                AggregateFactorySetting(
                    aggregate = ClassInfo(
                        packageName = it.aggregatePackageName,
                        className = it.aggregateClassName
                    ),
                    state = ClassInfo(
                        packageName = it.statePackageName,
                        className = it.stateClassName
                    ),
                    implementation = ClassInfo(
                        packageName = it.implementationPackageName,
                        className = it.implementationClassName
                    ),
                    isAbstract = it.isAbstract,
                    isEventSourced = it.isEventSourced
                )
            }

        return settings.copy(
            aggregateFactories = factories.toList()
        )
    }

    override fun shouldProcess(
        annotation: Class<out Annotation>,
        element: Element,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ): Boolean {
        return when (annotation) {
            EventSourced::class.java -> {
                element.kind.isClass
            }

            Implementation::class.java -> {
                element.kind.isClass && CodeUtility.isImplementInterface(
                    processingEnv,
                    element.asType(),
                    Aggregate::class.java.canonicalName,
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
            EventSourced::class.java -> {
                processElementsAnnotatedByEventSourced(elements, processingEnv)
            }

            Implementation::class.java -> {
                processElementsAnnotatedByImplementation(elements, processingEnv)
            }
        }
    }

    private fun processElementsAnnotatedByImplementation(
        elements: List<Element>,
        processingEnv: ProcessingEnvironment
    ) {
        elements.forEach {
            val key = processElement(it, processingEnv)

            data[key]!!.isAbstract = true
            if (null !== it.getAnnotation(EventSourced::class.java)) {
                data[key]!!.isEventSourced = true
            }
        }
    }

    private fun processElementsAnnotatedByEventSourced(elements: List<Element>, processingEnv: ProcessingEnvironment) {
        elements.forEach {
            val key = processElement(it, processingEnv)

            data[key]!!.isEventSourced = true
            if (null !== it.getAnnotation(Implementation::class.java)) {
                data[key]!!.isAbstract = true
            }
        }
    }

    private fun processElement(
        element: Element,
        processingEnv: ProcessingEnvironment
    ): String {
        val packageName = this.getPackageNameOfClass(element)
        val className = element.simpleName.toString()
        val key = "$packageName.$className"
        initCollectedFactoryIfNeeded(packageName, className)

        // find state type by 2 ways:
        // 1. scan and find the state field
        val stateField = element.enclosedElements.filter {
            it.kind.isField && it.simpleName.toString() == "state"
        }
        if (stateField.isNotEmpty()) {
            val stateType = stateField.first().asType()
            val state = processingEnv.typeUtils.asElement(stateType)
            data[key]!!.statePackageName = getPackageNameOfClass(state)
            data[key]!!.stateClassName = state.simpleName.toString()
        }

        // 2. find generic type of AbstractEventSourced class
        data[key]!!.extendsAbstractEventSourced = CodeUtility.isInheritClass(
            processingEnv, element, FrameworkProcessor.AbstractEventSourced, true
        )
        if (data[key]!!.extendsAbstractEventSourced) {
            val superclass =
                CodeUtility.findSuperClassElement(
                    processingEnv,
                    element,
                    FrameworkProcessor.AbstractEventSourced
                )
            if (null !== superclass) {
                if (superclass is DeclaredType) {
                    val stateType = superclass.typeArguments.first()
                    val state = processingEnv.typeUtils.asElement(stateType)
                    ContractCollector.collect(processingEnv, state)
                    data[key]!!.statePackageName = getPackageNameOfClass(state)
                    data[key]!!.stateClassName = state.simpleName.toString()
                }
            }
        }

        val aggregate = (element as TypeElement).interfaces.firstOrNull {
            CodeUtility.isImplementInterface(
                processingEnv,
                it,
                FrameworkProcessor.Aggregate
            )
        }
        if (null !== aggregate) {
            val aggregateElement = processingEnv.typeUtils.asElement(aggregate)
            data[key]!!.aggregatePackageName = getPackageNameOfClass(aggregateElement)
            data[key]!!.aggregateClassName = aggregateElement.simpleName.toString()
        }

        return key
    }

    private fun getPackageNameOfClass(element: Element): String {
        val upperElement = element.enclosingElement as? PackageElement ?: throw FoundationProcessorException(
            "@Implementation/@EventSourced do not support nested class."
        )

        return upperElement.qualifiedName.toString()
    }

    private fun initCollectedFactoryIfNeeded(packageName: String, className: String) {
        val key = "$packageName.$className"
        if (!data.containsKey(key)) {
            data[key] = CollectedFactory(
                aggregatePackageName = "",
                aggregateClassName = "",
                statePackageName = "",
                stateClassName = "",
                implementationPackageName = packageName,
                implementationClassName = className,
                isAbstract = false,
                isEventSourced = false,
                extendsAbstractEventSourced = false
            )
        }
    }
}