package com.generator.contract

import net.ntworld.foundation.cqrs.Query

interface GetUserByIdQuery : Query<GetUserByIdQueryResult> {
    val id: String
}
