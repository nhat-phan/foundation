package net.ntworld.foundation

/**
 * Annotation for interfaces which can be communicate via a [MessageBroker].
 *
 * The annotated interface could be:
 *  - [Event]
 *  - [Request]
 *  - [net.ntworld.foundation.cqrs.Command]
 *  - [net.ntworld.foundation.cqrs.Query]
 *
 * Foundation use this annotation to find a message channel when communicate via [MessageBroker].
 *
 */
@Target(AnnotationTarget.CLASS)
annotation class Messaging(
    /**
     * The channel which is used for communicate.
     */
    val channel: String = "",

    /**
     * Type of message, this will be added to [MessageAttribute] by [MessageTranslator]
     */
    val type: String = ""
)