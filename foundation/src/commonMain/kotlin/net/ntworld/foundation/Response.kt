package net.ntworld.foundation

interface Response : Contract {
    val error: Error?

    val isSuccess: Boolean get() = null === error

    val isFailure: Boolean get() = null !== error

    companion object
}
