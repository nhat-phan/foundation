package net.ntworld.foundation.generator;

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import java.text.SimpleDateFormat
import java.util.*

object Framework {
    internal var shouldAddHeader: Boolean = true

    val namespace = "net.ntworld.foundation"

    val EventData = ClassName("$namespace.eventSourcing", "EventData")

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