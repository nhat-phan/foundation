package net.ntworld.foundation.cqrs

interface ReceivedData<Q : FindByIdQuery<R>, R: QueryResult> {
    val id: String
    val result: R
}