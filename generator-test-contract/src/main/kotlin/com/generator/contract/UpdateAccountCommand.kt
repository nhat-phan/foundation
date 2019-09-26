package com.generator.contract

import net.ntworld.foundation.cqrs.Command

interface UpdateAccountCommand: Command, AccountProperties {
    val id: String

    companion object
}