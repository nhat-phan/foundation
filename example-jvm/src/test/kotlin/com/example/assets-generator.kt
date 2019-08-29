package com.example

import com.example.contract.CreateTodoCommand
import com.example.contract.GetAllTodoQuery
import net.ntworld.foundation.test.Contract
import net.ntworld.foundation.test.TestAssetsGenerator
import java.nio.file.Paths

val contracts = mapOf(
    "CreateTodoCommand" to Contract(CreateTodoCommand::class),
    "GetAllTodoQuery" to Contract(GetAllTodoQuery::class)
)

fun main() {
    val output = TestAssetsGenerator.generateContractFactory("com.example.testAsset", "ContractFactory", contracts)
    val root = Paths.get("").toAbsolutePath().toString()
    val path = Paths.get(root, "example-jvm", "src", "test", "kotlin", "com", "example", "testAsset", "ContractFactory.kt")
    path.toFile().writeText(output)
}
