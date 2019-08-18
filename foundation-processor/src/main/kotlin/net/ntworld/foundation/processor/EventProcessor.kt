package net.ntworld.foundation.processor

import net.ntworld.foundation.FrameworkAnnotation
import net.ntworld.foundation.eventSourcing.Encrypted
import net.ntworld.foundation.eventSourcing.Event
import net.ntworld.foundation.eventSourcing.Metadata
import net.ntworld.foundation.generator.EventDataGenerator
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.SettingsSerializer
import net.ntworld.foundation.generator.setting.EventSettings
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.EventField
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedAnnotationTypes(
    FrameworkAnnotation.EventType,
    FrameworkAnnotation.EventVariant,
    FrameworkAnnotation.Encrypted
)
@SupportedOptions(FrameworkAnnotation.KAPT_KOTLIN_GENERATED_OPTION_NAME)
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

        this.processElementsAnnotatedByEventType(roundEnv.getElementsAnnotatedWith(Event.Type::class.java))
        this.processElementsAnnotatedByEventVariant(roundEnv.getElementsAnnotatedWith(Event.Variant::class.java))
        this.processElementsAnnotatedByEncrypted(roundEnv.getElementsAnnotatedWith(Encrypted::class.java))
        this.processElementsAnnotatedByMetadata(roundEnv.getElementsAnnotatedWith(Metadata::class.java))

        val settings = translateCollectedDataToGeneratorSettings()

        val kaptKotlinGeneratedDir =
            processingEnv.options[FrameworkAnnotation.KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Can't find the target directory for generated Kotlin files."
                )
                return false
            }

        File(kaptKotlinGeneratedDir + "/log.json").writeText(SettingsSerializer.serialize(settings))

        return true
    }

    private fun processElementsAnnotatedByEventType(elements: Set<Element>) {
        elements
            .filter { it.kind.isClass }
            .forEach {
                val packageName = this.getPackageNameOfEvent(it)
                val className = it.simpleName.toString()
                val key = "$packageName.$className"
                initEventSettingsIfNeeded(packageName, className)

                it.enclosedElements.filter { it.kind == ElementKind.FIELD }
                    .forEach {
                        initEventFieldIfNeeded(packageName, className, it.simpleName.toString())
                    }

                data[key] = data[key]!!.copy(type = it.getAnnotation(Event.Type::class.java).type)
            }
    }

    private fun processElementsAnnotatedByEventVariant(elements: Set<Element>) {
        elements
            .filter { it.kind.isClass }
            .forEach {
                val packageName = this.getPackageNameOfEvent(it)
                val className = it.simpleName.toString()
                val key = "$packageName.$className"
                initEventSettingsIfNeeded(packageName, className)

                data[key] = data[key]!!.copy(variant = it.getAnnotation(Event.Variant::class.java).value)
            }
    }

    private fun processElementsAnnotatedByEncrypted(elements: Set<Element>) {
        elements
            .forEach {
                if (it.kind !== ElementKind.FIELD || it.enclosingElement.kind !== ElementKind.CLASS) {
                    throw FoundationProcessorException(
                        "@Encrypt only support field inside an Event class"
                    )
                }

                val packageName = this.getPackageNameOfEvent(it.enclosingElement)
                val className = it.enclosingElement.simpleName.toString()
                val fieldName = it.simpleName.toString()
                val key = "$packageName.$className"
                initEventSettingsIfNeeded(packageName, className)
                initEventFieldIfNeeded(packageName, className, fieldName)

                data[key]!!.fields[fieldName] = data[key]!!.fields[fieldName]!!.copy(
                    encrypted = true,
                    faked = it.getAnnotation(Encrypted::class.java).faked
                )
            }
    }

    private fun processElementsAnnotatedByMetadata(elements: Set<Element>) {
        elements
            .forEach {
                if (it.kind !== ElementKind.FIELD || it.enclosingElement.kind !== ElementKind.CLASS) {
                    throw FoundationProcessorException(
                        "@Encrypt only support field inside an Event class"
                    )
                }

                val packageName = this.getPackageNameOfEvent(it.enclosingElement)
                val className = it.enclosingElement.simpleName.toString()
                val fieldName = it.simpleName.toString()
                val key = "$packageName.$className"
                initEventSettingsIfNeeded(packageName, className)
                initEventFieldIfNeeded(packageName, className, fieldName)

                data[key]!!.fields[fieldName] = data[key]!!.fields[fieldName]!!.copy(
                    metadata = true
                )
            }
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