package net.ntworld.foundation.generator;

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import java.text.SimpleDateFormat
import java.util.*

internal object Framework {
    var shouldAddHeader: Boolean = true

    val namespace = "net.ntworld.foundation"

    val Infrastructure = ClassName(namespace, "Infrastructure")
    val InfrastructureProvider = ClassName(namespace, "InfrastructureProvider")

    val Faker = ClassName(namespace, "Faker")

    val Message = ClassName(namespace, "Message")
    val MessageConverter = ClassName(namespace, "MessageConverter")

    val EventData = ClassName("$namespace.eventSourcing", "EventData")
    val EventConverter = ClassName("$namespace.eventSourcing", "EventConverter")

    val EventMessageConverterUtility = ClassName("$namespace.eventSourcing", "EventMessageConverterUtility")
    val EventConverterUtility = ClassName("$namespace.eventSourcing", "EventConverterUtility")

    val FakerRelatedSource_FakedData = "FakedData"
    val FakerRelatedSource_JavaFakerWrapper_Jvm = "JavaFakerWrapper"

    val JavaFaker = ClassName("com.github.javafaker", "Faker")

    fun addGeneratedByToolHeader(file: FileSpec.Builder, generator: String?) {
        file.addComment("+-------------------------------------------------------------------------+\n")
        file.addComment("| This file was generated automatically by tools in foundation-generator. |\n")
        file.addComment("|                                                                         |\n")
        file.addComment("| Please do not edit!                                                     |\n")
        file.addComment("+-------------------------------------------------------------------------+\n")
        file.addComment("+- by  : $generator\n")
        file.addComment("+- when: ${now()}")
    }

    fun addFileHeader(file: FileSpec.Builder, generator: String?) {
        if (!shouldAddHeader) {
            return
        }

        file.addComment("+--------------------------------------------------------+\n")
        file.addComment("| This file was generated automatically in compile time. |\n")
        file.addComment("|                                                        |\n")
        file.addComment("| Please do not edit!                                    |\n")
        file.addComment("+--------------------------------------------------------+\n")
        file.addComment("+- by  : $generator\n")
        file.addComment("+- when: ${now()}")
    }

    private fun now(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(Date())
    }
}