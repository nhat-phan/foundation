package net.ntworld.foundation.cqrs

interface FindByIdQuery<Result> : Query<Result> {
    val id: String
}
