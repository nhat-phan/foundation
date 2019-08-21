package net.ntworld.foundation

import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.SettingsSerializer
import net.ntworld.foundation.processor.FoundationProcessorException
import java.nio.file.Paths
import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic

internal object Utility {
    fun readSettingsFile(processingEnv: ProcessingEnvironment): GeneratorSettings {
        val file = Paths.get(
            getKaptGeneratedDirectory(processingEnv),
            FrameworkProcessor.SETTINGS_PATH,
            FrameworkProcessor.SETTINGS_FILENAME
        ).toFile()
        if (!file.exists()) {
            return GeneratorSettings(events = listOf(), aggregateFactories = listOf())
        }
        val content = file.readText()
        return SettingsSerializer.parse(content)
    }

    fun updateSettingsFile(processingEnv: ProcessingEnvironment, settings: GeneratorSettings) {
        writeText(
            processingEnv,
            FrameworkProcessor.SETTINGS_PATH,
            FrameworkProcessor.SETTINGS_FILENAME,
            SettingsSerializer.serialize(settings)
        )
    }

    fun writeText(processingEnv: ProcessingEnvironment, directory: String, fileName: String, content: String) {
        val base = getKaptGeneratedDirectory(processingEnv)
        val dir = Paths.get(base, directory).toFile()
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = Paths.get(base, directory, fileName).toFile()
        file.writeText(content)
    }

    fun writeGeneratedFile(processingEnv: ProcessingEnvironment, file: GeneratedFile) {
        writeText(processingEnv, file.directory, file.fileName, file.content)
    }

    private fun getKaptGeneratedDirectory(processingEnv: ProcessingEnvironment): String {
        return processingEnv.options[FrameworkProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: run {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Can't find the target directory for generated Kotlin files."
            )
            throw FoundationProcessorException("Can't find the target directory for generated Kotlin files.")
        }
    }
}