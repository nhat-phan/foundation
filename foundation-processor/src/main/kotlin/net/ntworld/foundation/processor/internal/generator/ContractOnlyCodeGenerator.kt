package net.ntworld.foundation.processor.internal.generator

import net.ntworld.foundation.generator.ContractOnlyModeGenerator
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.processor.util.ProcessorOutput
import net.ntworld.foundation.processor.util.ProcessorSetting
import javax.annotation.processing.ProcessingEnvironment

internal class ContractOnlyCodeGenerator : CodeGenerator {
    override val mode: ProcessorSetting.Mode = ProcessorSetting.Mode.ContractOnly

    override fun generate(
        processingEnv: ProcessingEnvironment,
        processorSetting: ProcessorSetting,
        generatorSettings: GeneratorSettings
    ) {
        val file = ContractOnlyModeGenerator.generate(processorSetting.settingsClass, generatorSettings)
        ProcessorOutput.writeGeneratedFile(processingEnv, file)
    }
}