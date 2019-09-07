package com.generator.contract

import net.ntworld.foundation.cqrs.Command

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
