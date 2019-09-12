package net.ntworld.foundation.processor.internal.generator

import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.processor.util.ProcessorSetting
import javax.annotation.processing.ProcessingEnvironment

internal interface CodeGenerator {
    val mode: ProcessorSetting.Mode

    fun generate(
        processingEnv: ProcessingEnvironment,
        processorSetting: ProcessorSetting,
        generatorSettings: GeneratorSettings
    )
}
