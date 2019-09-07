package com.generator.queryHandler

import com.generator.UserInfrastructure
import com.generator.contract.GetUserByIdQuery
import com.generator.contract.GetUserByIdQueryResult
import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.QueryHandler

@Handler
class GetUserByIdQueryHandler(private val infrastructure: UserInfrastructure) :
    QueryHandler<GetUserByIdQuery, GetUserByIdQueryResult> {
    override fun handle(query: GetUserByIdQuery): GetUserByIdQueryResult {
        throw Exception("Not implemented")
    }
}