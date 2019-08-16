package net.ntworld.foundation

interface Message {
    val id: String?
    val type: String
    val attributes: Map<String, Any>
}
