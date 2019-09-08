package com.integration.request

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.Request

interface CreateUserRequest : Request<CreateUserResponse> {
    @Faked(FakedData.Internet.emailAddress)
    val email: String

    @Faked(FakedData.Name.fullName)
    val name: String

    companion object
}