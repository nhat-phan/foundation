package net.ntworld.foundation

/**
 * Interface for classes which handle Event. You can create as many handlers for the same event
 * as you want.
 *
 * **Usage**
 *
 * Given that you already have **MyEvent** interface which extends [Event]
 *
 * Then you create 2 handlers which will run when **MyEvent** is published
 *
 * ```kotlin
 * @Handler
 * class MyEventHandlerOne: EventHandler<MyEvent> {
 *   ...
 * }
 *
 * @Handler
 * class MyEventHandlerTwo: EventHandler<MyEvent> {
 *   ...
 * }
 * ```
 *
 * Thanks to [@Handler] the processor knows and will *generate*
 *
 * - **LocalEventBus** which wires your event handlers automatically
 *
 * The [EventHandler] is triggered whenever you use [EventBus]`.process(...)`
 *
 */
interface EventHandler<T : Event> {
    fun handle(event: T)

    @Suppress("UNCHECKED_CAST")
    fun execute(event: Event, message: Message?) = handle(event as T)
}
