package net.ntworld.foundation

interface ResolvableServiceBus : ServiceBus, LocalBusResolver<Request<*>, RequestHandler<*, *>>
