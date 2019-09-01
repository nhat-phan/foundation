package net.ntworld.foundation.processor

import net.ntworld.foundation.Infrastructure
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

internal object CodeUtility {
    val debug = mutableListOf<String>()

    fun isInheritOrImplement(
        processingEnv: ProcessingEnvironment,
        type: TypeMirror,
        qualifiedName: String
    ): Boolean {
        val checkImplement = isImplementInterface(processingEnv, type, qualifiedName, true)
        if (checkImplement) {
            return true
        }

        val element = processingEnv.typeUtils.asElement(type) as? TypeElement ?: return false
        val base = element.superclass
        val baseTypeElement = processingEnv.typeUtils.asElement(base) as? TypeElement ?: return false
        if (baseTypeElement.qualifiedName.toString() == qualifiedName) {
            return true
        }

        if (baseTypeElement.qualifiedName.toString() == java.lang.Object::class.java.canonicalName) {
            return false
        }

        return isInheritOrImplement(processingEnv, baseTypeElement.asType(), qualifiedName)
    }

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
        val baseTypeElement = processingEnv.typeUtils.asElement(base) as? TypeElement ?: return false
        if (baseTypeElement.qualifiedName.toString() == classNameQualifiedName) {
            return true
        }

        if (recursive && isInheritClass(
                processingEnv,
                baseTypeElement,
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

    fun canConstructedByInfrastructure(
        processingEnv: ProcessingEnvironment,
        element: TypeElement
    ): Boolean {
        val constructors = element.enclosedElements.filter { it.kind == ElementKind.CONSTRUCTOR }
        for (ctor in constructors) {
            if (ctor !is ExecutableElement) {
                continue
            }

            if (ctor.parameters.size == 1) {
                val firstParam = ctor.parameters.first()
                val typeElement = processingEnv.typeUtils.asElement(firstParam.asType()) as? TypeElement ?: return false
                if (typeElement.qualifiedName.toString() == Infrastructure::class.java.canonicalName) {
                    return true
                }
            }
        }
        return false
    }
}