package net.ntworld.foundation

interface Error {
    val type: String

    val message: String

    val code: Int
}