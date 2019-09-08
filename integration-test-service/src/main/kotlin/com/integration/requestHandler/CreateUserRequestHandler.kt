package com.integration.requestHandler

import com.integration.request.CreateUserRequest
import com.integration.request.CreateUserResponse
import com.integration.request.make
import net.ntworld.foundation.Handler
import net.ntworld.foundation.RequestHandler
import net.ntworld.foundation.generated.ErrorImpl

@Handler
class CreateUserRequestHandler : RequestHandler<CreateUserRequest, CreateUserResponse> {
    override fun handle(request: CreateUserRequest): CreateUserResponse {
        return CreateUserResponse.make(ErrorImpl("NotImplemented", "Not implemented", 0))
    }
}