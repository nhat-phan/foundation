package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.ClassName
import net.ntworld.foundation.generator.setting.*
import net.ntworld.foundation.generator.type.ClassInfo
import java.lang.Math.min

internal object Utility {
    fun findLocalEventBusTarget(settings: List<EventHandlerSetting>, namespace: String? = null): ClassInfo {
        val packageName = resolvePackageNameForLocalBuses(namespace, settings)

        return ClassInfo(
            className = "LocalEventBus",
            packageName = packageName
        )
    }

    fun findLocalCommandBusTarget(settings: List<CommandHandlerSetting>, namespace: String? = null): ClassInfo {
        val packageName = resolvePackageNameForLocalBuses(namespace, settings)

        return ClassInfo(
            className = "LocalCommandBus",
            packageName = packageName
        )
    }

    fun findMockableCommandBusTarget(settings: List<CommandHandlerSetting>, namespace: String? = null): ClassInfo {
        val packageName = resolvePackageNameForLocalBuses(namespace, settings)

        return ClassInfo(
            className = "MockableCommandBus",
            packageName = packageName
        )
    }

    fun findLocalQueryBusTarget(settings: List<QueryHandlerSetting>, namespace: String? = null): ClassInfo {
        val packageName = resolvePackageNameForLocalBuses(namespace, settings)

        return ClassInfo(
            className = "LocalQueryBus",
            packageName = packageName
        )
    }

    fun findLocalServiceBusTarget(settings: List<RequestHandlerSetting>, namespace: String? = null): ClassInfo {
        val packageName = resolvePackageNameForLocalBuses(namespace, settings)

        return ClassInfo(
            className = "LocalServiceBus",
            packageName = packageName
        )
    }

    fun findMessageChannelDictionaryTarget(contracts: List<ClassInfo>, namespace: String? = null): ClassInfo {
        val packageName = resolvePackageName(namespace) {
            var name = ""
            contracts.forEach {
                name = this.guessPackageName(name, it.packageName)
            }
            name
        }

        return ClassInfo(
            className = "MessageChannelDictionary",
            packageName = packageName
        )
    }


    fun findContractFactoryTarget(factories: List<ClassInfo>, namespace: String? = null): ClassInfo {
        val packageName = resolvePackageName(namespace) {
            var name = ""
            factories.forEach {
                name = this.guessPackageName(name, it.packageName)
            }
            name
        }

        return ClassInfo(
            className = "ContractFactory",
            packageName = packageName
        )
    }

    fun findContractFactoryTargetForTest(factories: List<ClassInfo>, namespace: String? = null): ClassInfo {
        val packageName = resolvePackageName(namespace) {
            var name = ""
            factories.forEach {
                name = this.guessPackageName(name, it.packageName)
            }
            name
        }

        return ClassInfo(
            className = "ContractFactoryTest",
            packageName = packageName
        )
    }

    fun findUtilityTargetForTest(classes: List<ClassInfo>, namespace: String? = null): ClassInfo {
        val packageName = resolvePackageName(namespace) {
            var name = ""
            classes.forEach {
                name = this.guessPackageName(name, it.packageName)
            }
            name
        }

        return ClassInfo(
            className = "TestUtility",
            packageName = packageName
        )
    }

    fun findContractImplementationTarget(setting: ContractSetting): ClassInfo {
        return ClassInfo(
            className = "${setting.contract.className}Impl",
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

    fun findMessageTranslatorTarget(setting: ContractSetting): ClassInfo {
        return ClassInfo(
            className = "${setting.contract.className}MessageTranslator",
            packageName = findTargetNamespace(setting.contract.packageName, ".messageTranslator")
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

    fun findInfrastructureProviderTarget(settings: GeneratorSettings, namespace: String? = null): ClassInfo {
        val packageName = resolvePackageName(namespace) {
            var name = ""
            settings.aggregateFactories.forEach {
                name = this.guessPackageName(name, it.implementation.packageName)
            }
            settings.eventSourcings.forEach {
                name = this.guessPackageName(name, it.implementation.packageName)
            }
            settings.eventHandlers.forEach {
                name = this.guessPackageName(name, it.handler.packageName)
            }
            settings.requestHandlers.forEach {
                name = this.guessPackageName(name, it.handler.packageName)
            }
            settings.commandHandlers.forEach {
                name = this.guessPackageName(name, it.handler.packageName)
            }
            settings.queryHandlers.forEach {
                name = this.guessPackageName(name, it.handler.packageName)
            }
            name
        }
        return ClassInfo(
            packageName = packageName,
            className = "AutoGeneratedInfrastructureProvider"
        )
    }

    private fun resolvePackageName(namespace: String?, resolver: () -> String): String {
        if (null !== namespace) {
            return namespace
        }
        return resolver.invoke()
    }

    private fun resolvePackageNameForLocalBuses(namespace: String?, settings: List<HandlerSetting>): String {
        return resolvePackageName(namespace) {
            var name = ""
            settings.forEach {
                name = this.guessPackageName(name, it.handler.packageName)
            }
            name
        }
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

    private fun findTargetNamespace(input: String, prefix: String = ""): String {
        return "$input$prefix.generated"
    }

    fun stringToClassInfo(name: String): ClassInfo {
        val parts = name.split('.')
        if (parts.size == 1) {
            return ClassInfo(packageName = "", className = name)
        }
        val packageParts = mutableListOf<String>()
        for (i in 0 until parts.lastIndex) {
            packageParts.add(parts[i])
        }
        return ClassInfo(packageName = packageParts.joinToString("."), className = parts[parts.lastIndex])
    }

    fun stringToClassName(name: String): ClassName {
        return stringToClassInfo(name).toClassName()
    }
}