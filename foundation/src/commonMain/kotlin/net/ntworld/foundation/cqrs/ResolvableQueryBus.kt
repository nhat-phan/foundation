package net.ntworld.foundation.cqrs

import net.ntworld.foundation.LocalBusResolver

interface ResolvableQueryBus : QueryBus, LocalBusResolver<Query<*>, QueryHandler<*, *>>
