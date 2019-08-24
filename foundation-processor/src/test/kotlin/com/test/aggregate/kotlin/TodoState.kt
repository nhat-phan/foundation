package com.test.aggregate.kotlin

import net.ntworld.foundation.State

data class TodoState(override val id: String, override val isGenerated: Boolean) : State