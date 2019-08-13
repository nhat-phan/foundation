package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Aggregate

data class Snapshot<T : Aggregate>(val aggregate: T, val version: Int)