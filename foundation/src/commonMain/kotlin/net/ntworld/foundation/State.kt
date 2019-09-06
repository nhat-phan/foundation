package net.ntworld.foundation

interface State : Contract {
    val id: String

    val isGenerated: Boolean
}