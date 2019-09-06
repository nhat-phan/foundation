package net.ntworld.foundation

interface Response<T> : Contract {
    val error: Error?

    val value: T

    val isSuccess: Boolean get() = null === error

    val isFailure: Boolean get() = null !== error
}
