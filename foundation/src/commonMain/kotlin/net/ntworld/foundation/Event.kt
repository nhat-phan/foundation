package net.ntworld.foundation

/**
 * Base interface of all kinds of event, including event for EventSourcing provided in
 * [net.ntworld.foundation.eventSourcing] package
 *
 *
 * Only interfaces extend from [Event] can be handled by [EventHandler]
 *
 *
 * **Usage**
 *
 * Create **MyEvent** interface which extends [Event] interface
 *
 * ```kotlin
 * interface MyEvent: Event {
 *   val name: String
 * }
 * ```
 *
 * Then create a handler for **MyEvent**
 *
 * ```kotlin
 * @Handler
 * class MyEventHandler: EventHandler<MyEvent> {
 *   ...
 * }
 * ```
 *
 * Thanks to [@Handler] the processor knows and will *generate*
 *
 * - An implementation for **MyEvent**
 * - **LocalEventBus** which wires your event handlers automatically
 */
interface Event : Contract
