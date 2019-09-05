package net.ntworld.foundation.cqrs

interface FindByIdQuery<R: QueryResult> : Query<R> {
    val id: String
}
