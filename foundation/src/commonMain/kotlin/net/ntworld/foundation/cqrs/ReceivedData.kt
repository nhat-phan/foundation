package net.ntworld.foundation.cqrs

interface ReceivedData<Q : FindByIdQuery<R>, R> {
    val id: String
    val result: R
}