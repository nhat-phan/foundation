package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.*

class MutableGeneratorSettings(private val settings: GeneratorSettings) {
    private val aggregateFactories = toMutableMap(settings.aggregateFactories)
    private val events = toMutableMap(settings.eventSourcings)
    private val eventHandlers = toMutableMap(settings.eventHandlers)
    private val commandHandlers = toMutableMap(settings.commandHandlers)
    private val queryHandlers = toMutableMap(settings.queryHandlers)
    private val implementations = toMutableMap(settings.implementations)
    private val messages = toMutableMap(settings.messages)
    private val contracts = toMutableMap(settings.contracts)

    private fun <T : Setting> toMutableMap(data: List<T>): MutableMap<String, T> {
        val map = mutableMapOf<String, T>()
        data.forEach {
            map[it.name] = it
        }
        return map
    }

    private fun <T : Setting> toList(map: Map<String, T>): List<T> {
        return map.values.toList()
    }

    fun toGeneratorSettings(): GeneratorSettings {
        return settings.copy(
            aggregateFactories = toList(aggregateFactories),
            eventSourcings = toList(events),
            eventHandlers = toList(eventHandlers),
            commandHandlers = toList(commandHandlers),
            queryHandlers = toList(queryHandlers),
            implementations = toList(implementations),
            messages = toList(messages),
            contracts = toList(contracts)
        )
    }

    fun hasAggregateFactory(name: String): Boolean {
        return aggregateFactories.containsKey(name)
    }

    fun hasEventSourcing(name: String): Boolean {
        return events.containsKey(name)
    }

    fun hasEventHandler(name: String): Boolean {
        return eventHandlers.containsKey(name)
    }

    fun hasCommandHandler(name: String): Boolean {
        return commandHandlers.containsKey(name)
    }

    fun hasQueryHandler(name: String): Boolean {
        return queryHandlers.containsKey(name)
    }

    fun hasImplementation(name: String): Boolean {
        return implementations.containsKey(name)
    }

    fun hasMessage(name: String): Boolean {
        return messages.containsKey(name)
    }

    fun hasContract(name: String): ContractSetting? {
        return contracts[name]
    }

    fun getAggregateFactory(name: String): AggregateFactorySetting? {
        return aggregateFactories[name]
    }

    fun getEventSourcing(name: String): EventSourcingSetting? {
        return events[name]
    }

    fun getEventHandler(name: String): EventHandlerSetting? {
        return eventHandlers[name]
    }

    fun getCommandHandler(name: String): CommandHandlerSetting? {
        return commandHandlers[name]
    }

    fun getQueryHandler(name: String): QueryHandlerSetting? {
        return queryHandlers[name]
    }

    fun getImplementation(name: String): ImplementationSetting? {
        return implementations[name]
    }

    fun getMessage(name: String): MessageSetting? {
        return messages[name]
    }

    fun getContract(name: String): ContractSetting? {
        return contracts[name]
    }

    fun <T : Setting> put(setting: T): MutableGeneratorSettings {
        when (setting) {
            is AggregateFactorySetting -> aggregateFactories[setting.name] = setting
            is EventSourcingSetting -> events[setting.name] = setting
            is EventHandlerSetting -> eventHandlers[setting.name] = setting
            is CommandHandlerSetting -> commandHandlers[setting.name] = setting
            is QueryHandlerSetting -> queryHandlers[setting.name] = setting
            is ImplementationSetting -> implementations[setting.name] = setting
            is MessageSetting -> messages[setting.name] = setting
            is ContractSetting -> contracts[setting.name] = setting
        }
        return this
    }
}
