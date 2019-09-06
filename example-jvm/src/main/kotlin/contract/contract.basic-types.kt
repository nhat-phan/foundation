package com.example.contract

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.CommandHandler

@Faked(type = FakedData.Internet.emailAddress)
annotation class EmailFaked

annotation class NoAffectedFaked

interface BasicTypeContract : Command {
    val byte: Byte
    val short: Short
    val int: Int
    val long: Long
    val float: Float
    val double: Double
    val string: String
    val char: Char
    val boolean: Boolean
    val byteNullable: Byte?
    val shortNullable: Short?
    val intNullable: Int?
    val longNullable: Long?
    val floatNullable: Float?
    val doubleNullable: Double?
    val stringNullable: String?
    val charNullable: Char?
    val booleanNullable: Boolean?
}

@Handler
class BasicTypeContractCommandHandler : CommandHandler<BasicTypeContract> {
    override fun handle(command: BasicTypeContract) {
    }
}