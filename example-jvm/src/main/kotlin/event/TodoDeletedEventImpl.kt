package event

import com.example.event.TodoDeletedEvent
import kotlinx.serialization.Serializable
import net.ntworld.foundation.Implementation
import net.ntworld.foundation.eventSourcing.EventSourcing

@Implementation
@Serializable
@EventSourcing(type = "deleted", variant = 0)
data class TodoDeletedEventImpl(override val id: String) : TodoDeletedEvent