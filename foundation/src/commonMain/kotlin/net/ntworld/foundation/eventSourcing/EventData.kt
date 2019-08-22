package net.ntworld.foundation.eventSourcing

interface EventData {
    val id: String
    val type: String
    val variant: Int
    val streamId: String
    val streamType: String
    val version: Int
    val data: String
    val metadata: String

    val stream: String
        get() = "$streamType:$streamId"
}
