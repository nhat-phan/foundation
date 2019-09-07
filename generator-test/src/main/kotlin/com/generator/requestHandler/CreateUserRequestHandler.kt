package com.generator.requestHandler

import com.generator.contract.CreateUserRequest
import com.generator.contract.CreateUserResponse
import net.ntworld.foundation.Handler
import net.ntworld.foundation.RequestHandler

@Handler
class CreateUserRequestHandler(companyId: Int) : RequestHandler<CreateUserRequest, CreateUserResponse> {
    override fun handle(request: CreateUserRequest): CreateUserResponse {
        throw Exception("Not implemented")
    }
}