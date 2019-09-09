package com.generator

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class LoadSetting(val settings: String)
