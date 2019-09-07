package com.generator.contract

import net.ntworld.foundation.Error
import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData

interface DefaultValueContract : Error {
    @get:Faked(FakedData.Number.randomNumber)
    override val code: Int

    override val type: String
        get() = "contract.TodoError"

    companion object
}
