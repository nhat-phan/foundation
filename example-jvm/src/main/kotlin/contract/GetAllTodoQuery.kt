package com.example.contract

import net.ntworld.foundation.Messaging
import net.ntworld.foundation.cqrs.Query

@Messaging(channel = "todo")
interface GetAllTodoQuery : Query<List<String>> {

}