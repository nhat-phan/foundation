package com.generator.contract

import net.ntworld.foundation.cqrs.Command

interface CustomTypeContract : Command {
    val name: String

    val address: CustomTypeContractAddress
}

interface CustomTypeContractAddress {
    val number: Int

    val street: String
}
