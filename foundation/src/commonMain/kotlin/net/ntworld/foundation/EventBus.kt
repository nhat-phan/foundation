package net.ntworld.foundation

interface EventBus {
    infix fun publish(event: Event)

    infix fun process(event: Event)
}
