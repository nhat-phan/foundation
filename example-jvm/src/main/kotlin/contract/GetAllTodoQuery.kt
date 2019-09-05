package com.example.contract

import net.ntworld.foundation.Messaging
import net.ntworld.foundation.cqrs.Query
import net.ntworld.foundation.cqrs.QueryResult

@Messaging(channel = "todo")
interface GetAllTodoQuery : Query<GetAllTodoQueryResult>

interface GetAllTodoQueryResult: QueryResult {
    val data: List<String>
}