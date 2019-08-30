package net.ntworld.foundation.processor

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

internal object CodeUtility {
    fun isImplementInterface(
        processingEnv: ProcessingEnvironment,
        type: TypeMirror,
        interfaceNameQualifiedName: String,
        recursive: Boolean = false
    ): Boolean {
        val element = processingEnv.typeUtils.asElement(type) as? TypeElement ?: return false
        for (base in element.interfaces) {
            val baseElement = processingEnv.typeUtils.asElement(base) as? TypeElement ?: return false
            if (baseElement.qualifiedName.toString() == interfaceNameQualifiedName) {
                return true
            }

            if (recursive && isImplementInterface(
                    processingEnv,
                    base,
                    interfaceNameQualifiedName,
                    recursive
                )
            ) {
                return true
            }
        }
        return false
    }

    fun isInheritClass(
        processingEnv: ProcessingEnvironment,
        element: Element,
        classNameQualifiedName: String,
        recursive: Boolean = false
    ): Boolean {
        val casted = element as? TypeElement ?: return false
        val base = casted.superclass
        val baseElement = processingEnv.typeUtils.asElement(base) as? TypeElement ?: return false
        if (baseElement.qualifiedName.toString() == classNameQualifiedName) {
            return true
        }

        if (recursive && isInheritClass(
                processingEnv,
                baseElement,
                classNameQualifiedName,
                recursive
            )
        ) {
            return true
        }

        return false
    }

    fun findSuperClassElement(
        processingEnv: ProcessingEnvironment,
        element: Element,
        classNameQualifiedName: String
    ): TypeMirror? {
        val casted = element as? TypeElement ?: return null
        val base = casted.superclass
        val baseElement = processingEnv.typeUtils.asElement(base) as? TypeElement ?: return null
        if (baseElement.qualifiedName.toString() == classNameQualifiedName) {
            return base
        }
        return findSuperClassElement(
            processingEnv,
            baseElement,
            classNameQualifiedName
        )
    }

}