package net.ntworld.foundation.processor.util

import javax.annotation.processing.ProcessingEnvironment

internal data class ProcessorSetting(
    val mode: Mode,
    val globalNamespace: String?,
    val settingsClass: String?,
    val isDev: Boolean
) {
    enum class Mode {
        Default,
        ContractOnly
    }

    companion object {
        private val DEV_OPTION_NAME = listOf(
            "1",
            "true",
            "yes"
        )
        private val CONTRACT_ONLY_MODE_VALUES = listOf(
            "lib",
            "library",
            "sharedContract",
            "sharedContracts",
            "shared-contract",
            "shared-contracts",
            "contract",
            "contracts",
            "contractOnly",
            "contractsOnly",
            "contract-only",
            "contracts-only"
        )

        fun read(processingEnv: ProcessingEnvironment): ProcessorSetting {
            return ProcessorSetting(
                mode = readMode(processingEnv),
                globalNamespace = readGlobalNamespace(processingEnv),
                settingsClass = readSettingsClass(processingEnv),
                isDev = readIsDev(processingEnv)
            )
        }

        private fun readMode(processingEnv: ProcessingEnvironment): Mode {
            val mode = processingEnv.options[FrameworkProcessor.MODE_OPTION_NAME]
            if (null !== mode && CONTRACT_ONLY_MODE_VALUES.contains(mode.trim())) {
                return Mode.ContractOnly
            }
            return Mode.Default
        }

        private fun readSettingsClass(processingEnv: ProcessingEnvironment): String? {
            return processingEnv.options[FrameworkProcessor.SETTINGS_CLASS_OPTION_NAME]
        }

        private fun readGlobalNamespace(processingEnv: ProcessingEnvironment): String? {
            return processingEnv.options[FrameworkProcessor.GLOBAL_NAMESPACE_OPTION_NAME]
        }

        private fun readIsDev(processingEnv: ProcessingEnvironment): Boolean {
            val dev = processingEnv.options[FrameworkProcessor.DEV_OPTION_NAME]
            if (null !== dev && DEV_OPTION_NAME.contains(dev.toLowerCase().trim())) {
                return true
            }
            return false
        }
    }
}