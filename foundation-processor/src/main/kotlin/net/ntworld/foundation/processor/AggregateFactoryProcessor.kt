package net.ntworld.foundation.processor

import net.ntworld.foundation.*
import net.ntworld.foundation.eventSourcing.EventSourced
import net.ntworld.foundation.generator.AggregateFactoryGenerator
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.InfrastructureProviderGenerator
import net.ntworld.foundation.generator.setting.AggregateFactorySetting
import net.ntworld.foundation.generator.type.ClassInfo
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

// TODO: This is old style, will be upgraded to use 1 single AbstractProcessor for all kinds
@SupportedAnnotationTypes(
    FrameworkProcessor.Implementation,
    FrameworkProcessor.EventSourced
)
@SupportedOptions(FrameworkProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@Deprecated(message = "Will be rewrite soon", level = DeprecationLevel.WARNING)
class AggregateFactoryProcessor : AbstractProcessor() {
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

    private val debug = mutableListOf<String>()
    private val data: MutableMap<String, CollectedFactory> = mutableMapOf()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        if (null === annotations || null === roundEnv) {
            return true
        }

        this.processElementsAnnotatedByImplementation(roundEnv.getElementsAnnotatedWith(Implementation::class.java))
        this.processElementsAnnotatedByEventSourced(roundEnv.getElementsAnnotatedWith(EventSourced::class.java))

        val settings = ProcessorOutput.readSettingsFile(processingEnv)
        val collectedSettings = translateCollectedDataToGeneratorSettings()
        val mergedSettings = settings.copy(
            description = debug.joinToString("\n"),
            aggregateFactories = collectedSettings.aggregateFactories
        )

        ProcessorOutput.updateSettingsFile(processingEnv, mergedSettings)
        settings.aggregateFactories.forEach {
            ProcessorOutput.writeGeneratedFile(processingEnv, AggregateFactoryGenerator.generate(it))
        }

        return true
    }

    private fun processElementsAnnotatedByImplementation(elements: Set<Element>) {
        elements
            .filter {
                it.kind.isClass && CodeUtility.isImplementInterface(
                    processingEnv,
                    it.asType(),
                    Aggregate::class.java.canonicalName,
                    true
                )
            }
            .forEach {
                val key = processElement(it, true)

                data[key]!!.isAbstract = true
                if (null !== it.getAnnotation(EventSourced::class.java)) {
                    data[key]!!.isEventSourced = true
                }
            }
    }

    private fun processElementsAnnotatedByEventSourced(elements: Set<Element>) {
        elements
            .filter { it.kind.isClass }
            .forEach {
                val key = processElement(it, false)

                data[key]!!.isEventSourced = true
                if (null !== it.getAnnotation(Implementation::class.java)) {
                    data[key]!!.isAbstract = true
                }
            }
    }

    private fun processElement(element: Element, processingImplementation: Boolean): String {
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
            val superclass = CodeUtility.findSuperClassElement(processingEnv, element, FrameworkProcessor.AbstractEventSourced)
            if (null !== superclass) {
                if (superclass is DeclaredType) {
                    val stateType = superclass.typeArguments.first()
                    val state = processingEnv.typeUtils.asElement(stateType)
                    data[key]!!.statePackageName = getPackageNameOfClass(state)
                    data[key]!!.stateClassName = state.simpleName.toString()
                }
            }
        }

        // find aggregate by find the aggregate interface which implementation implement
        /** Reserve for processing Implementation by type + contract later
        if (processingImplementation) {
            val mirrors = element.annotationMirrors
            mirrors.forEach {
                if (it.annotationType.toString() == FrameworkProcessor.Implementation) {
                    it.elementValues.forEach {
                        debug.add(it.key.toString())
                        debug.add(it.value.value.toString())
                    }
                }

            }
        }
        */
        val aggregate = (element as TypeElement).interfaces.firstOrNull {
            CodeUtility.isImplementInterface(processingEnv, it, FrameworkProcessor.Aggregate)
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

    private fun translateCollectedDataToGeneratorSettings(): GeneratorSettings {
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
                    name = "${it.implementationPackageName}.${it.implementationClassName}",
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

        return GeneratorSettings(
            provider = "",
            aggregateFactories = factories.toList(),
            events = emptyList()
        )
    }
}