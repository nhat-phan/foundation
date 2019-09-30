package net.ntworld.foundation.cqrs

import net.ntworld.foundation.LocalBusResolver

interface ResolvableCommandBus : CommandBus, LocalBusResolver<Command, CommandHandler<*>>
