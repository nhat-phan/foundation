package test.xxx.event

import net.ntworld.foundation.FakedData
import net.ntworld.foundation.eventSourcing.Encrypted
import net.ntworld.foundation.eventSourcing.Event
import net.ntworld.foundation.eventSourcing.Metadata

@Event.Type("todo:created")
data class TodoCreatedEvent(

    val id: String,

    @Metadata
    val companyId: String,

    @Encrypted(faked = FakedData.GameOfThrones.character)
    val task: String,

    val createdAt: String

) : Event
