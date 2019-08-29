package com.example.entity

import net.ntworld.foundation.*
import net.ntworld.foundation.cqrs.*


// contracts
interface SettingData {
    val id: String

    @Faked(type = FakedData.Name.fullName)
    val language: String

    @Faked(type = FakedData.Name.fullName)
    val timezone: String

    @Faked(type = FakedData.Name.fullName)
    val subscription: String
}

interface FindSettingByIdQuery : FindByIdQuery<SettingData> {
    override val id: String
}

// Kotlin test faked data => in run-time
fun main() {
    val type = SettingData::class
    type.members.forEach {
        val prop = it
        println(prop.name)
        prop.annotations.forEach {
            println(it.toString())
        }
        println("-----")
    }
}

class ContractFactory(private val faker: Faker) {
    private data class SettingDataImpl(
        override val id: String,
        override val language: String,
        override val timezone: String,
        override val subscription: String
    ) : SettingData

    @Suppress("UNCHECKED_CAST")
    private fun <T> createFakedData(type: String): T {
        return faker.makeFakeData(type) as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> readMap(data: Map<String, Any>, key: String, fakerType: String): T {
        return if (data.containsKey(key)) {
            data[key] as T
        } else {
            faker.makeFakeData(fakerType) as T
        }
    }

    fun makeSettingData(id: String, data: Map<String, Any>): SettingData =
        SettingDataImpl(
            id = id,
            language = readMap(data, "language", "language-type"),
            timezone = readMap(data, "timezone", "timezone-type"),
            subscription = readMap(data, "subscription", "timezone-type")
        )

    fun makeSettingData(id: String): SettingData =
        SettingDataImpl(
            id = id,
            language = createFakedData("language-type"),
            timezone = createFakedData("language-type"),
            subscription = createFakedData("language-type")
        )

    fun makeSettingData(id: String, language: String): SettingData =
        SettingDataImpl(
            id = id,
            language = language,
            timezone = createFakedData("timezone-type"),
            subscription = createFakedData("subscription-type")
        )

    fun makeSettingData(id: String, language: String, timezone: String): SettingData =
        SettingDataImpl(
            id = id,
            language = language,
            timezone = timezone,
            subscription = createFakedData("subscription-type")
        )

    fun makeSettingData(id: String, language: String, timezone: String, subscription: String): SettingData =
        SettingDataImpl(
            id = id,
            language = language,
            timezone = timezone,
            subscription = subscription
        )
}


// implementation
@Implementation
data class Setting(
    override val id: String,
    override val result: SettingData
) : ReceivedData<FindSettingByIdQuery, SettingData> {
    val timezone: String = result.timezone
}


// generated automatically

data class FindSettingByIdQueryImpl(
    override val id: String
) : FindSettingByIdQuery

// This one will be generated automatically if there is a ReceivedData using it
data class SettingDataImpl(
    override val id: String,
    override val language: String,
    override val timezone: String,
    override val subscription: String
) : SettingData

// ----------------------------------------------------------------

interface TodoState : State {}
interface Todo : Aggregate<TodoState> {}

interface ChangeSomethingCommand : Command {}

class ChangeSomethingCommandHandler(
    private val infrastructure: Infrastructure
) : CommandHandler<ChangeSomethingCommand> {
    override fun handle(command: ChangeSomethingCommand) {
        // In command, there are 4 steps
        //   $1. Get all affected aggregates via factoryOf(...).generateContractFactory() or factoryOf(...).retrieve()
        //   x2. Get all info you need to perform business logic => HERE
        //   $3a. Perform aggregate business logic
        //   $3b. Controls your related business logic (saga job) via commandBus().process(...)
        //   $4. Persist aggregate via save(...)
        use(infrastructure) {
            val todo = factoryOf(Todo::class).generate()
            val setting = receiverOf(Setting::class).find("id")

            val result = eventBus()

            // step 3a
            // step 3b

            // step 4
            save(todo)
        }

        infrastructure {
            val todo = factoryOf(Todo::class).generate()
            val setting = receiverOf(Setting::class).find("id")
            val result = eventBus()

            // step 3a
            // step 3b

            // step 4

        }
    }
}

class FindSettingByIdQueryHandler(
    private val infrastructure: Infrastructure
) : QueryHandler<FindSettingByIdQuery, SettingData> {
    override fun handle(query: FindSettingByIdQuery): SettingData {
        return execute(query, null)
    }

    override fun execute(query: FindSettingByIdQuery, message: Message?): SettingData {
        // In command, there are - steps
        //   $1. Get all info you need from your domain
        //   $2. Get all info you need from other domain via queryBus().process(...)
        //   $3. Transform data to correct Result type
        return use(infrastructure) {
            // val result = eventBus()
            val result = queryBus()
            // step 3a
            // step 3b

            // step 4
            SettingDataImpl("", "", "", "")
        }
    }
}

//@RetrieveBy(query = FindSettingByIdQuery::class)
//data class SettingData(
//    override val id: String,
//    override val state: SettingData
//): Aggregate<Setting> {
//
//    val language = state.language
//    val timeZone = state.timezone
//
//}


// Auto generated code

// generated factory
//class SettingEntityFactory(private val infrastructure: Infrastructure) {
//    fun retrieve(id: String): SettingData? {
//        val result: Setting? = infrastructure.queryBus().process(FindSettingByIdQueryImpl(id = id))
//        if (null === result) {
//            return null
//        }
//
//        return SettingData(
//            id = id,
//            state = result
//        )
//    }
//}
//
//fun client(infrastructure: Infrastructure) {
//    // SettingEntityFactory(infrastructure).retrieve("test")!!.
//}