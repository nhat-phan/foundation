package net.ntworld.foundation.generator

import net.ntworld.foundation.generator.setting.*

class MutableGeneratorSettings(private val settings: GeneratorSettings) {
    private val aggregateFactories = toMutableMap(settings.aggregateFactories)
    private val eventSourcings = toMutableMap(settings.eventSourcings)
    private val eventHandlers = toMutableMap(settings.eventHandlers)
    private val commandHandlers = toMutableMap(settings.commandHandlers)
    private val queryHandlers = toMutableMap(settings.queryHandlers)
    private val requestHandlers = toMutableMap(settings.requestHandlers)
    private val implementations = toMutableMap(settings.implementations)
    private val messages = toMutableMap(settings.messages)
    private val contracts = toMutableMap(settings.contracts)
    private val fakedAnnotations = toMutableMap(settings.fakedAnnotations)
    private val fakedProperties = toMutableMap(settings.fakedProperties)

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
            eventSourcings = toList(eventSourcings),
            eventHandlers = toList(eventHandlers),
            commandHandlers = toList(commandHandlers),
            queryHandlers = toList(queryHandlers),
            implementations = toList(implementations),
            messages = toList(messages),
            contracts = toList(contracts),
            fakedAnnotations = toList(fakedAnnotations),
            fakedProperties = toList(fakedProperties)
        )
    }

    fun merge(input: GeneratorSettings): MutableGeneratorSettings {
        mergeItems(input.aggregateFactories, aggregateFactories)
        mergeItems(input.eventSourcings, eventSourcings)
        mergeItems(input.eventHandlers, eventHandlers)
        mergeItems(input.commandHandlers, commandHandlers)
        mergeItems(input.queryHandlers, queryHandlers)
        mergeItems(input.implementations, implementations)
        mergeItems(input.messages, messages)
        mergeItems(input.contracts, contracts)
        mergeItems(input.fakedAnnotations, fakedAnnotations)
        mergeItems(input.fakedProperties, fakedProperties)
        return this
    }

    private fun <T : Setting> mergeItems(source: List<T>, target: MutableMap<String, T>) {
        source.forEach {
            target[it.name] = it
        }
    }

    fun hasAggregateFactory(name: String): Boolean = aggregateFactories.containsKey(name)
    fun hasEventSourcing(name: String): Boolean = eventSourcings.containsKey(name)
    fun hasEventHandler(name: String): Boolean = eventHandlers.containsKey(name)
    fun hasCommandHandler(name: String): Boolean = commandHandlers.containsKey(name)
    fun hasQueryHandler(name: String): Boolean = queryHandlers.containsKey(name)
    fun hasRequestHandler(name: String): Boolean = requestHandlers.containsKey(name)
    fun hasImplementation(name: String): Boolean = implementations.containsKey(name)
    fun hasMessage(name: String): Boolean = messages.containsKey(name)
    fun hasContract(name: String): ContractSetting? = contracts[name]
    fun hasFakedAnnotationSetting(name: String): FakedAnnotationSetting? = fakedAnnotations[name]
    fun hasFakedPropertySetting(name: String): FakedPropertySetting? = fakedProperties[name]

    fun getAggregateFactory(name: String): AggregateFactorySetting? = aggregateFactories[name]
    fun getEventSourcing(name: String): EventSourcingSetting? = eventSourcings[name]
    fun getEventHandler(name: String): EventHandlerSetting? = eventHandlers[name]
    fun getCommandHandler(name: String): CommandHandlerSetting? = commandHandlers[name]
    fun getQueryHandler(name: String): QueryHandlerSetting? = queryHandlers[name]
    fun getRequestHandler(name: String): RequestHandlerSetting? = requestHandlers[name]
    fun getImplementation(name: String): ImplementationSetting? = implementations[name]
    fun getMessage(name: String): MessageSetting? = messages[name]
    fun getContract(name: String): ContractSetting? = contracts[name]
    fun getFakedAnnotationSetting(name: String): FakedAnnotationSetting? = fakedAnnotations[name]
    fun getFakedPropertySetting(name: String): FakedPropertySetting? = fakedProperties[name]

    fun <T : Setting> put(setting: T): MutableGeneratorSettings {
        when (setting) {
            is AggregateFactorySetting -> aggregateFactories[setting.name] = setting
            is EventSourcingSetting -> eventSourcings[setting.name] = setting
            is EventHandlerSetting -> eventHandlers[setting.name] = setting
            is CommandHandlerSetting -> commandHandlers[setting.name] = setting
            is QueryHandlerSetting -> queryHandlers[setting.name] = setting
            is ImplementationSetting -> implementations[setting.name] = setting
            is MessageSetting -> messages[setting.name] = setting
            is ContractSetting -> contracts[setting.name] = setting
            is FakedAnnotationSetting -> fakedAnnotations[setting.name] = setting
            is FakedPropertySetting -> fakedProperties[setting.name] = setting
        }
        return this
    }
}
