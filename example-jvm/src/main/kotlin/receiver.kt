package com.example.entity

import net.ntworld.foundation.*
import net.ntworld.foundation.cqrs.*


// contracts
interface SettingData {
    val id: String
    val language: String
    val timezone: String
    val subscription: String
}

interface FindSettingByIdQuery : FindByIdQuery<SettingData> {
    override val id: String
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
        //   $1. Get all affected aggregates via factoryOf(...).generate() or factoryOf(...).retrieve()
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
        return handle(query, null)
    }

    override fun handle(query: FindSettingByIdQuery, message: Message?): SettingData {
        // In command, there are - steps
        //   $1. Get all info you need from your domain
        //   $2. Get all info you need from other domain via queryBus().process(...)
        //   $3. Transform data to correct Result type
        return use(infrastructure) {
            val result = eventBus()
            // val result = queryBus()
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