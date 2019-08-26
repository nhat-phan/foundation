package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.FileSpec
import java.text.SimpleDateFormat
import java.util.*

object GeneratorOutput {
    private var isTest = false

    fun setupTest() {
        isTest = true
    }

    fun tearDownTest() {
        isTest = false
    }

    fun addToolHeader(file: FileSpec.Builder, generator: String?) {
        file.addComment("+-------------------------------------------------------------------------+\n")
        file.addComment("| This file was generated automatically by tools in foundation-generator. |\n")
        file.addComment("|                                                                         |\n")
        file.addComment("| Please do not edit!                                                     |\n")
        file.addComment("+-------------------------------------------------------------------------+\n")
        file.addComment("+- by  : $generator\n")
        file.addComment("+- when: ${now()}")
    }

    fun addHeader(file: FileSpec.Builder, generator: String?) {
        if (isTest) {
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