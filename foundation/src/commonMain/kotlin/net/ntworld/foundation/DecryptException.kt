package net.ntworld.foundation

class DecryptException(
    val previous: Exception? = null
) : Exception(
    if (previous !== null) previous.message else null
)