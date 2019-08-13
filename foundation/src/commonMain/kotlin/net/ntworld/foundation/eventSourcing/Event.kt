package net.ntworld.foundation.eventSourcing

interface Event {
    @Target(AnnotationTarget.CLASS)
    annotation class Type(val type: String)

    @Target(AnnotationTarget.CLASS)
    annotation class Variant(val value: Int)
}