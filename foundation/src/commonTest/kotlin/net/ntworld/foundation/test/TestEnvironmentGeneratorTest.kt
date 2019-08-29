package net.ntworld.foundation.test

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import kotlin.test.Test

class TestEnvironmentGeneratorTest {
    interface CreateUserCommand {
        @Faked(type = FakedData.Internet.emailAddress)
        val email: String

        @Faked(type = FakedData.Internet.password)
        val password: String

        @Faked(type = FakedData.Address.city)
        val city: String?
    }

    @Test
    fun testGenerateContractFactories() {
        val list = mapOf(
            "CreateUserCommand" to Contract(
                definition = CreateUserCommand::class,
                fields = listOf(
                    CreateUserCommand::email,
                    CreateUserCommand::password,
                    CreateUserCommand::city
                )
            )
        )

        val output = TestEnvironmentGenerator.generateContractFactory(
            "net.ntworld.foundation.test",
            "ContractFactory",
            list
        )
        println(output)
    }
}