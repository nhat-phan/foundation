package com.generator.contract

import net.ntworld.foundation.Response

interface CreateUserResponse : Response {
    val data: User
}