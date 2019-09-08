package com.generator.contract

import net.ntworld.foundation.Request

interface GetUserByIdRequest : Request<GetUserByIdResponse> {
    val id: String
}
