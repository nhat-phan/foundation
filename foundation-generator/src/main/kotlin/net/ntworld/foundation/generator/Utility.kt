package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.*
import net.ntworld.foundation.generator.type.ClassInfo
import java.lang.Math.min

internal object Utility {
    fun findLocalEventBusTarget(settings: List<EventHandlerSetting>, namespace: String? = null): ClassInfo {
        var packageName = if (null !== namespace) namespace else ""
        settings.forEach {
            packageName = this.guessPackageName(packageName, it.handler.packageName)
        }
        return ClassInfo(
            className = "LocalEventBus",
            packageName = packageName
        )
    }

    fun findLocalCommandBusTarget(settings: List<CommandHandlerSetting>, namespace: String? = null): ClassInfo {
        var packageName = if (null !== namespace) namespace else ""
        settings.forEach {
            packageName = this.guessPackageName(packageName, it.handler.packageName)
        }
        return ClassInfo(
            className = "LocalCommandBus",
            packageName = packageName
        )
    }

    fun findLocalQueryBusTarget(settings: List<QueryHandlerSetting>, namespace: String? = null): ClassInfo {
        var packageName = if (null !== namespace) namespace else ""
        settings.forEach {
            packageName = this.guessPackageName(packageName, it.handler.packageName)
        }
        return ClassInfo(
            className = "LocalQueryBus",
            packageName = packageName
        )
    }

    fun findLocalServiceBusTarget(settings: List<RequestHandlerSetting>, namespace: String? = null): ClassInfo {
        var packageName = if (null !== namespace) namespace else ""
        settings.forEach {
            packageName = this.guessPackageName(packageName, it.handler.packageName)
        }
        return ClassInfo(
            className = "LocalServiceBus",
            packageName = packageName
        )
    }

    fun findContractFactoryTarget(factories: List<ClassInfo>, namespace: String? = null): ClassInfo {
        var packageName = if (null !== namespace) namespace else ""
        factories.forEach {
            packageName = this.guessPackageName(packageName, it.packageName)
        }
        return ClassInfo(
            className = "ContractFactory",
            packageName = packageName
        )
    }

    fun findContractImplementationTarget(setting: ContractSetting): ClassInfo {
        return ClassInfo(
            className = "${setting.contract.className}Impl",
            packageName = findTargetNamespace(setting.contract.packageName)
        )
    }

    fun findContractImplementationFactoryTarget(setting: ContractSetting): ClassInfo {
        return ClassInfo(
            className = "${setting.contract.className}Factory",
            packageName = findTargetNamespace(setting.contract.packageName)
        )
    }

    fun findEventConverterTarget(setting: EventSourcingSetting): ClassInfo {
        return ClassInfo(
            className = "${setting.implementation.className}Converter",
            packageName = findTargetNamespace(setting.event.packageName)
        )
    }

    fun findEventEntityTarget(setting: EventSourcingSetting): ClassInfo {
        return ClassInfo(
            className = "${setting.implementation.className}Entity",
            packageName = findTargetNamespace(setting.event.packageName)
        )
    }

    fun findEventMessageTranslatorTarget(setting: EventSourcingSetting): ClassInfo {
        return ClassInfo(
            className = "${setting.implementation.className}MessageTranslator",
            packageName = findTargetNamespace(setting.event.packageName)
        )
    }

    fun findAggregateFactoryTarget(setting: AggregateFactorySetting): ClassInfo {
        val name = "${setting.aggregate.className}Factory"
        if (setting.isAbstract) {
            return ClassInfo(
                className = "Abstract$name",
                packageName = findTargetNamespace(setting.implementation.packageName)
            )
        }
        return ClassInfo(
            className = "$name",
            packageName = findTargetNamespace(setting.implementation.packageName)
        )
    }

    fun buildMainGeneratedFile(target: ClassInfo, content: String) =
        buildGeneratedFile(target, content, GeneratedFile.Type.Main)

    fun buildTestGeneratedFile(target: ClassInfo, content: String) =
        buildGeneratedFile(target, content, GeneratedFile.Type.Test)

    private fun buildGeneratedFile(target: ClassInfo, content: String, type: GeneratedFile.Type): GeneratedFile {
        val directory = target.packageName.replace(".", "/")
        val fileName = target.className + ".kt"
        return GeneratedFile(
            type = type,
            target = target,
            directory = "/$directory",
            fileName = fileName,
            path = "/$directory/$fileName",
            content = content
        )
    }

    fun findInfrastructureProviderTarget(settings: GeneratorSettings, namespace: String? = null): ClassInfo {
        var packageName = if (null !== namespace) namespace else ""
        settings.aggregateFactories.forEach {
            packageName = this.guessPackageName(packageName, it.implementation.packageName)
        }
        settings.eventSourcings.forEach {
            packageName = this.guessPackageName(packageName, it.implementation.packageName)
        }
        settings.eventHandlers.forEach {
            packageName = this.guessPackageName(packageName, it.handler.packageName)
        }
        settings.requestHandlers.forEach {
            packageName = this.guessPackageName(packageName, it.handler.packageName)
        }
        settings.commandHandlers.forEach {
            packageName = this.guessPackageName(packageName, it.handler.packageName)
        }
        settings.queryHandlers.forEach {
            packageName = this.guessPackageName(packageName, it.handler.packageName)
        }
        return ClassInfo(
            packageName = packageName,
            className = "AutoGeneratedInfrastructureProvider"
        )
    }

    internal fun guessPackageName(current: String, given: String): String {
        when {
            current.isEmpty() -> return given
            given.isEmpty() -> return current
            given.indexOf(current) == 0 -> return current
            current.indexOf(given) == 0 -> return given
        }

        val currentParts = current.split(".")
        val givenParts = given.split(".")
        val parts = mutableListOf<String>()
        val lastIndex = min(currentParts.lastIndex, givenParts.lastIndex)
        for (i in 0..lastIndex) {
            if (currentParts[i] == givenParts[i]) {
                parts.add(currentParts[i])
            }
        }
        return if (parts.isEmpty()) current else parts.joinToString(".")
    }

    private fun findTargetNamespace(input: String): String {
        return "$input.generated"
    }
}