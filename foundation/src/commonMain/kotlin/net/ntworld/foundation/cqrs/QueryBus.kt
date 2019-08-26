package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Message

interface QueryBus {
    fun <R> process(query: Query<R>): R = process(query, null)

    fun <R> process(query: Query<R>, message: Message?): R
}


interface GetSomethingQuery: Query<Int> {

}

interface GetSomethingElseQuery: Query<String> {

}

//class GetSomethingQueryHandler: QueryHandler<GetSomethingQuery, Int> {
//    override fun handle(query: GetSomethingQuery, message: Message?): Int {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//}

fun client(bus: QueryBus, x: GetSomethingQuery, y: GetSomethingElseQuery) {

}