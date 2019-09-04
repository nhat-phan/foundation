package com.example.contract

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.Command
import net.ntworld.foundation.cqrs.CommandHandler
import kotlin.reflect.KClass

// --------------------------------------------------------------------------------

@Faked(type = FakedData.Internet.emailAddress)
annotation class EmailFaked

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

// --------------------------------------------------------------------------------

interface ListTypeContract : Command {
    val listByte: List<Byte>
    val listOfNullableByte: List<Byte?>
    val nullableListOfByte: List<Byte>?
    val nullableListOfNullableByte: List<Byte?>?
    val listString: List<String>
    val listOfNullableString: List<String?>
    val nullableListOfString: List<String>?
    val nullableListOfNullableString: List<String?>?
}

@Handler
class ListTypeContractHandler : CommandHandler<ListTypeContract> {
    override fun handle(command: ListTypeContract) {
    }
}

// --------------------------------------------------------------------------------

annotation class NoAffectedFaked

interface NoSupertypeContractCommand : Command {
    @get:Faked(type = FakedData.Zelda.character)
    val zelda: String

    @Faked(type = FakedData.StarTrek.character)
    val name: String

    @EmailFaked
    val email: String?

    @NoAffectedFaked
    val list: List<String>

    val phones: List<Int>
}

@Handler
class NoSupertypeContractCommandHandler : CommandHandler<NoSupertypeContractCommand> {
    override fun handle(command: NoSupertypeContractCommand) {
    }
}

// --------------------------------------------------------------------------------

interface OneSupertypeContractParent {
    @get:Faked(type = FakedData.Zelda.character)
    val zelda: String

    @Faked(type = FakedData.Name.firstName)
    val firstName: String

    @Faked(type = FakedData.Name.lastName)
    val lastName: String
}

interface OneSupertypeContract : OneSupertypeContractParent, Command {
    @EmailFaked
    val email: String
}

@Handler
class OneSupertypeContractHandler : CommandHandler<OneSupertypeContract> {
    override fun handle(command: OneSupertypeContract) {
    }
}

// --------------------------------------------------------------------------------

interface OneSupertypeOverrideContractParent {
    @get:Faked(type = FakedData.Zelda.character)
    val zelda: String

    @Faked(type = FakedData.Name.firstName)
    val firstName: String

    @Faked(type = FakedData.Name.lastName)
    val lastName: String
}

interface OneSupertypeOverrideContract : OneSupertypeOverrideContractParent, Command {
    @EmailFaked
    val email: String

    override val firstName: String

    override val lastName: String
}

@Handler
class OneSupertypeOverrideContractHandler : CommandHandler<OneSupertypeOverrideContract> {
    override fun handle(command: OneSupertypeOverrideContract) {
    }
}

//// --------------------------------------------------------------------------------
//
//object OneSupertypeContractFactory {
//    fun make(zelda: String, firstName: String, lastName: String, email: String): OneSupertypeContract {
//        TODO()
//    }
//}
//
//object OneSupertypeOverrideContractFactory {
//    fun make(zelda: String, email: String, firstName: String, lastName: String): OneSupertypeOverrideContract {
//        TODO()
//    }
//}
//
//object ContractFactory {
//    fun of(contract: KClass<OneSupertypeContract>): OneSupertypeContractFactory {
//        return OneSupertypeContractFactory
//    }
//
//    fun of(contract: KClass<OneSupertypeOverrideContract>): OneSupertypeOverrideContractFactory {
//        return OneSupertypeOverrideContractFactory
//    }
//}
//
//fun main() {
//    println(ContractFactory.of(OneSupertypeContract::class))
//    println(ContractFactory.of(OneSupertypeOverrideContract::class))
//}