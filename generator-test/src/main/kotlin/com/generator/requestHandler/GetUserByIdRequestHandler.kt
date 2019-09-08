package com.generator.requestHandler

import com.generator.UserInfrastructure
import com.generator.contract.GetUserByIdRequest
import com.generator.contract.GetUserByIdResponse
import net.ntworld.foundation.Handler
import net.ntworld.foundation.RequestHandler

@Handler
class GetUserByIdRequestHandler(private val infrastructure: UserInfrastructure) :
    RequestHandler<GetUserByIdRequest, GetUserByIdResponse> {
    override fun handle(request: GetUserByIdRequest): GetUserByIdResponse {
        throw Exception("Not implemented")
    }
}