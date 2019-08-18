package net.ntworld.foundation.eventSourcing

@Target(AnnotationTarget.CLASS)
annotation class EventSourcing(
    val type: String,
    val variant: Int = 0
) {
    @Target(AnnotationTarget.FIELD)
    annotation class Encrypted(val faked: String = "")

    @Target(AnnotationTarget.FIELD)
    annotation class Metadata
}