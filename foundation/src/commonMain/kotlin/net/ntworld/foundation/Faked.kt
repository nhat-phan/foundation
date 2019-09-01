package net.ntworld.foundation

@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.ANNOTATION_CLASS
)
annotation class Faked(val type: String)
