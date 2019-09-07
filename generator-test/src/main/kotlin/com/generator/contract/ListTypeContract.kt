package com.generator.contract

import net.ntworld.foundation.cqrs.Command

interface ListTypeContract : Command {
    val listByte: List<Byte>
    val listOfNullableByte: List<Byte?>
    val nullableListOfByte: List<Byte>?
    val nullableListOfNullableByte: List<Byte?>?
    val listString: List<String>
    val listOfNullableString: List<String?>
    val nullableListOfString: List<String>?
    val nullableListOfNullableString: List<String?>?

    companion object
}
