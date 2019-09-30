package net.ntworld.foundation

@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY
)
annotation class DefaultArgument(val code: String)
