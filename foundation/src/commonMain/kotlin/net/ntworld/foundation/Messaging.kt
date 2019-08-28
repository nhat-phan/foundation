package net.ntworld.foundation

@Target(AnnotationTarget.CLASS)
annotation class Messaging(val channel: String = "")