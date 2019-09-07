package com.generator.contract

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.cqrs.Command

interface CustomTypeListContract: Command {
    @Faked(FakedData.Name.fullName)
    val name: String

    val addresses: List<CustomTypeListContractAddress>

    companion object
}

interface CustomTypeListContractAddress {
    val number: Int

    val street: String

    @Faked(FakedData.Address.city)
    val city: String

    companion object
}
