package net.ntworld.foundation.eventSourcing

@Target(AnnotationTarget.CLASS)
annotation class EventSourced(val isWrapper: Boolean = true)
