package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.State

data class Snapshot<T : State>(val state: T, val version: Int)