//package com.example.testAsset
//
//import com.example.contract.CreateTodoCommand
//import com.example.contract.GetAllTodoQuery
//import kotlin.Any
//import kotlin.String
//import kotlin.Suppress
//import kotlin.collections.Map
//import net.ntworld.foundation.Faker
//
//class ContractFactory(private val faker: Faker) {
//    @Suppress("UNCHECKED_CAST")
//    fun <T> readMap(
//        data: Map<String, Any>,
//        key: String,
//        fakerType: String
//    ): T = if (data.containsKey(key)) data[key] as T else faker.makeFakeData(fakerType) as T
//
//    @Suppress("UNCHECKED_CAST")
//    fun <T> createFakedData(type: String): T = faker.makeFakeData(type) as T
//
//    fun makeCreateTodoCommand(data: Map<String, Any>): CreateTodoCommand =
//            CreateTodoCommandTestImpl(
//        task = readMap(data, "task", "starTrek.location")
//    )
//
//    fun makeCreateTodoCommand(): CreateTodoCommand = CreateTodoCommandTestImpl(
//        task = createFakedData("starTrek.location")
//    )
//
//    fun makeCreateTodoCommand(task: String): CreateTodoCommand = CreateTodoCommandTestImpl(
//        task = task
//    )
//
//    fun makeGetAllTodoQuery(): GetAllTodoQuery = GetAllTodoQueryTestImpl(
//    )
//
//    private class CreateTodoCommandTestImpl(override val task: String) : CreateTodoCommand
//
//    private class GetAllTodoQueryTestImpl() : GetAllTodoQuery
//}
