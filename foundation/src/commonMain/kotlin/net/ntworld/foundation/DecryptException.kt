package net.ntworld.foundation

import net.ntworld.foundation.eventSourcing.Event

class DecryptException(event: Event, field: String) : Exception("Cannot decrypt $field in $event")