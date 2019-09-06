package net.ntworld.foundation.processor.util

import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.SettingsSerializer
import net.ntworld.foundation.processor.FoundationProcessorException
import java.nio.file.Paths
import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic

internal object ProcessorOutput {
    const val PROCESSOR_VERSION = "0.3.x"
    private var isTest: Boolean = false

    private val files: MutableMap<String, String> = mutableMapOf()

    val output: Map<String, String> = files

    fun setupTest() {
        isTest = true
        files.clear()
    }

    fun tearDownTest() {
        isTest = false
        files.clear()
    }

    private fun initGeneratorSettings() = GeneratorSettings(
        description = "",
        processorVersion = PROCESSOR_VERSION,
        globalDirectory = "",
        annotationProcessorRunInfo = listOf(),
        eventSourcings = listOf(),
        aggregateFactories = listOf(),
        eventHandlers = listOf(),
        commandHandlers = listOf(),
        queryHandlers = listOf(),
        requestHandlers = listOf(),
        implementations = listOf(),
        messages = listOf(),
        contracts = listOf(),
        fakedAnnotations = listOf()
    )

    fun deleteFile(processingEnv: ProcessingEnvironment, path: String) {
        if (isTest) {
            return
        }

        val base = getKaptGeneratedDirectory(processingEnv)
        val file = Paths.get(base, path).toFile()
        if (file.exists()) {
            file.delete()
        }
    }

    fun readSettingsFile(processingEnv: ProcessingEnvironment, kaptTest: Boolean = false): GeneratorSettings {
        if (isTest) {
            val path = FrameworkProcessor.SETTINGS_PATH + "/" + FrameworkProcessor.SETTINGS_FILENAME
            if (!files.contains(path)) {
                return initGeneratorSettings()
            }
            return SettingsSerializer.parse(files[path]!!)
        }

        var base = getKaptGeneratedDirectory(processingEnv)
        if (kaptTest) {
            base = Paths.get(base).resolve("../main").normalize().toString()
        }
        val file = Paths.get(
            base,
            FrameworkProcessor.SETTINGS_PATH,
            FrameworkProcessor.SETTINGS_FILENAME
        ).toFile()
        if (!file.exists()) {
            return initGeneratorSettings()
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

    private fun writeText(
        processingEnv: ProcessingEnvironment,
        directory: String,
        fileName: String,
        content: String
    ) {
        if (isTest) {
            files["$directory/$fileName"] = content
            return
        }

        val base = getKaptGeneratedDirectory(processingEnv)
        val dir = Paths.get(base, directory).toFile()
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = Paths.get(base, directory, fileName).toFile()
        file.writeText(content)
    }

    fun writeGeneratedFile(processingEnv: ProcessingEnvironment, file: GeneratedFile) {
        writeText(
            processingEnv,
            file.directory,
            file.fileName,
            file.content
        )
    }

    fun writeGlobalFile(processingEnv: ProcessingEnvironment, settings: GeneratorSettings, file: GeneratedFile) {
        if (settings.globalDirectory.isNotEmpty()) {
            deleteFile(
                processingEnv,
                settings.globalDirectory + "/" + file.fileName
            )
        }
        val mergedSettings = settings.copy(
            globalDirectory = file.directory
        )
        writeText(
            processingEnv,
            file.directory,
            file.fileName,
            file.content
        )
        updateSettingsFile(processingEnv, mergedSettings)
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