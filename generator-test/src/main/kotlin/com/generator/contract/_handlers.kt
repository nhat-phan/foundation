package com.generator.contract

import net.ntworld.foundation.Handler
import net.ntworld.foundation.Implementation
import net.ntworld.foundation.Infrastructure
import net.ntworld.foundation.cqrs.CommandHandler

@Handler
class BasicTypeContractHandler : CommandHandler<BasicTypeContract> {
    override fun handle(command: BasicTypeContract) {
    }
}

@Implementation
class DefaultValueContractException(
    override val message: String,
    override val code: Int = 0
) : Exception(), DefaultValueContract

@Handler
class ListTypeContractHandler(
    infrastructure: Infrastructure
) : CommandHandler<ListTypeContract> {
    override fun handle(command: ListTypeContract) {
    }
}

@Handler
class NoSupertypeContractCommandHandler : CommandHandler<NoSupertypeContract> {
    override fun handle(command: NoSupertypeContract) {
    }
}

@Handler
class OneSupertypeContractHandler : CommandHandler<OneSupertypeContract> {
    override fun handle(command: OneSupertypeContract) {
    }
}

@Handler
class OneSupertypeOverrideContractHandler : CommandHandler<OneSupertypeOverrideContract> {
    override fun handle(command: OneSupertypeOverrideContract) {
    }
}

@Handler
class CustomTypeContractHandler : CommandHandler<CustomTypeContract> {
    override fun handle(command: CustomTypeContract) {
    }
}

@Handler
class CustomTypeListContractHandler : CommandHandler<CustomTypeListContract> {
    override fun handle(command: CustomTypeListContract) {
    }
}
