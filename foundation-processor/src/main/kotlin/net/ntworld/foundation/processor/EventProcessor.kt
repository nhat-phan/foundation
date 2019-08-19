package net.ntworld.foundation.processor

import net.ntworld.foundation.FrameworkProcessor
import net.ntworld.foundation.Utility
import net.ntworld.foundation.eventSourcing.EventSourcing
import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.EventField
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes(
    FrameworkProcessor.EventSourcing,
    FrameworkProcessor.Metadata,
    FrameworkProcessor.Encrypted
)
@SupportedOptions(FrameworkProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class EventProcessor : AbstractProcessor() {
    internal data class CollectedEventField(
        val name: String,
        val metadata: Boolean,
        val encrypted: Boolean,
        val faked: String
    )

    internal data class CollectedEvent(
        val packageName: String,
        val className: String,
        val fields: MutableMap<String, CollectedEventField>,
        val type: String,
        val variant: Int
    )

    private val data: MutableMap<String, CollectedEvent> = mutableMapOf()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        if (null === annotations || null === roundEnv) {
            return true
        }

        this.processElementsAnnotatedByEventSourcing(roundEnv.getElementsAnnotatedWith(EventSourcing::class.java))
        this.processElementsAnnotatedByEncrypted(roundEnv.getElementsAnnotatedWith(EventSourcing.Encrypted::class.java))
        this.processElementsAnnotatedByMetadata(roundEnv.getElementsAnnotatedWith(EventSourcing.Metadata::class.java))

        val settings = Utility.readSettingsFile(processingEnv)
        val collectedSettings = translateCollectedDataToGeneratorSettings()
        val mergedSettings = settings.copy(
            events = collectedSettings.events
        )

        Utility.updateSettingsFile(processingEnv, mergedSettings)
        settings.events.forEach {
            Utility.writeGeneratedFile(processingEnv, EventDataGenerator.generate(it))
            Utility.writeGeneratedFile(processingEnv, EventConverterGenerator.generate(it))
            Utility.writeGeneratedFile(processingEnv, EventDataMessageConverterGenerator.generate(it))
        }
        Utility.writeGeneratedFile(processingEnv, InfrastructureProviderGenerator().generate(mergedSettings))

        return true
    }

    private fun processElementsAnnotatedByEventSourcing(elements: Set<Element>) {
        elements
            .filter { it.kind.isClass }
            .forEach {
                val packageName = this.getPackageNameOfEvent(it)
                val className = it.simpleName.toString()
                val key = "$packageName.$className"
                initEventSettingsIfNeeded(packageName, className)

                val annotation = it.getAnnotation(EventSourcing::class.java)
                it.enclosedElements.filter { it.kind == ElementKind.FIELD }
                    .forEach {
                        initEventFieldIfNeeded(packageName, className, it.simpleName.toString())
                    }

                data[key] = data[key]!!.copy(
                    type = annotation.type,
                    variant = annotation.variant
                )
            }
    }

    private fun processElementsAnnotatedByEncrypted(elements: Set<Element>) {
        elements
            .forEach {
                this.collectEventField(it) { item ->
                    item.copy(
                        encrypted = true,
                        faked = it.getAnnotation(EventSourcing.Encrypted::class.java).faked
                    )
                }
            }
    }

    private fun processElementsAnnotatedByMetadata(elements: Set<Element>) {
        elements
            .forEach {
                this.collectEventField(it) { item -> item.copy(metadata = true) }
            }
    }

    private fun collectEventField(element: Element, block: (item: CollectedEventField) -> CollectedEventField) {
        if (element.kind !== ElementKind.FIELD || element.enclosingElement.kind !== ElementKind.CLASS) {
            throw FoundationProcessorException(
                "@Encrypt only support field inside an Event class"
            )
        }

        val packageName = this.getPackageNameOfEvent(element.enclosingElement)
        val className = element.enclosingElement.simpleName.toString()
        val fieldName = element.simpleName.toString()
        val key = "$packageName.$className"
        initEventSettingsIfNeeded(packageName, className)
        initEventFieldIfNeeded(packageName, className, fieldName)

        data[key]!!.fields[fieldName] = block.invoke(data[key]!!.fields[fieldName]!!)
    }

    private fun getPackageNameOfEvent(element: Element): String {
        val upperElement = element.enclosingElement as? PackageElement ?: throw FoundationProcessorException(
            "@Event.Type do not support nested class."
        )

        return upperElement.qualifiedName.toString()
    }

    private fun initEventFieldIfNeeded(packageName: String, className: String, field: String) {
        val key = "$packageName.$className"
        if (!data[key]!!.fields.containsKey(field)) {
            data[key]!!.fields[field] = CollectedEventField(
                name = field,
                metadata = false,
                encrypted = false,
                faked = ""
            )
        }

    }

    private fun initEventSettingsIfNeeded(packageName: String, className: String) {
        val key = "$packageName.$className"
        if (!data.containsKey(key)) {
            data[key] = CollectedEvent(
                packageName = packageName,
                className = className,
                fields = mutableMapOf(),
                type = "",
                variant = 0
            )
        }
    }

    private fun translateCollectedDataToGeneratorSettings(): GeneratorSettings {
        val events = data.values.map {
            val fields = it.fields.values.map {
                EventField(name = it.name, metadata = it.metadata, encrypted = it.encrypted, faked = it.faked)
            }
            EventSettings(
                event = ClassInfo(packageName = it.packageName, className = it.className),
                fields = fields,
                type = it.type,
                variant = it.variant
            )
        }

        return GeneratorSettings(
            events = events.toList()
        )
    }
}