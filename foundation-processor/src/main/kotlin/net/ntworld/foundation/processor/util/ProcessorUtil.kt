package net.ntworld.foundation.processor.util

import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.main.InfrastructureProviderMainGenerator

internal object ProcessorUtil {
    fun findGlobalNamespace(processorSetting: ProcessorSetting, generatorSettings: GeneratorSettings): String {
        if (null === processorSetting.globalNamespace || processorSetting.globalNamespace.isEmpty()) {
            return InfrastructureProviderMainGenerator.findTarget(generatorSettings).packageName
        }
        return processorSetting.globalNamespace
    }
}