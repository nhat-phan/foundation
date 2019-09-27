package net.ntworld.foundation.processor.internal.processor

import net.ntworld.foundation.Messaging
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.MessagingSetting
import net.ntworld.foundation.generator.type.ClassInfo
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

class MessagingProcessor : Processor {
    override val annotations: List<Class<out Annotation>> = listOf(
        Messaging::class.java
    )

    internal data class CollectedMessaging(
        val packageName: String,
        val className: String,
        val channel: String,
        val type: String
    )

    private val data = mutableMapOf<String, CollectedMessaging>()

    override fun startProcess(settings: GeneratorSettings) {
        data.clear()
        settings.messagings.forEach { item ->
            data[item.name] = CollectedMessaging(
                packageName = item.contract.packageName,
                className = item.contract.className,
                channel = item.channel,
                type = item.type
            )
        }
    }

    override fun applySettings(settings: GeneratorSettings): GeneratorSettings {
        val messagingSetting = data.values
            .filter {
                it.packageName.isNotEmpty() && it.className.isNotEmpty() && (
                    it.channel.isNotEmpty() || it.type.isNotEmpty()
                )
            }
            .map {
                MessagingSetting(
                    contract = ClassInfo(
                        packageName = it.packageName,
                        className = it.className
                    ),
                    channel = it.channel,
                    type = it.type
                )
            }
        return settings.copy(messagings = messagingSetting)
    }

    override fun shouldProcess(
        annotation: Class<out Annotation>,
        element: Element,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ): Boolean {
        return when (annotation) {
            Messaging::class.java -> {
                element.kind == ElementKind.INTERFACE || element.kind == ElementKind.CLASS
            }

            else -> false
        }
    }

    override fun process(
        annotation: Class<out Annotation>,
        elements: List<Element>,
        processingEnv: ProcessingEnvironment,
        roundEnv: RoundEnvironment
    ) = elements.forEach {
        val packageElement = processingEnv.elementUtils.getPackageOf(it)
        val className = it.simpleName.toString()
        val key = "${packageElement.qualifiedName}.$className"

        val messagingAnnotation = it.getAnnotation(Messaging::class.java)
        if (null === messagingAnnotation) {
            return@forEach
        }

        data[key] = CollectedMessaging(
            packageName = packageElement.qualifiedName.toString(),
            className = className,
            channel = messagingAnnotation.channel,
            type = messagingAnnotation.type
        )
    }

}