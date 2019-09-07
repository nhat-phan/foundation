package com.generator.contract

import com.generator.annotation.EmailFaked
import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.Request

interface CreateUserRequest : Request<CreateUserResponse> {
    @Faked(FakedData.Name.fullName)
    val name: String

    @EmailFaked
    val email: String

    val phone: String
}