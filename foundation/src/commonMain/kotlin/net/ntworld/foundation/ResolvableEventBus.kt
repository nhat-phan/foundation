package net.ntworld.foundation

interface ResolvableEventBus : EventBus, LocalBusResolver<Event, Array<EventHandler<*>>>