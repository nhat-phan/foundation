package com.example.contract

import net.ntworld.foundation.Error
import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.Implementation

interface DefaultValueContract : Error {
    @get:Faked(FakedData.Number.randomNumber)
    override val code: Int

    override val type: String
        get() = "contract.TodoError"

    companion object
}

@Implementation
class TodoException(override val message: String, override val code: Int = 0) : Exception(), DefaultValueContract
