package net.ntworld.foundation

interface Error : Contract {
    val type: String

    val message: String

    val code: Int
}