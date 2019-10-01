package net.ntworld.foundation.cqrs

import net.ntworld.foundation.Contract

interface Query<R : QueryResult> : Contract {

    companion object
}
