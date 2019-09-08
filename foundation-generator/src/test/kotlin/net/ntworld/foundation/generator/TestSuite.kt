package net.ntworld.foundation.generator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.fail

open class TestSuite {
    @BeforeTest
    fun setUp() {
        GeneratorOutput.setupTest()
    }

    @AfterTest
    fun tearDown() {
        GeneratorOutput.tearDownTest()
    }

    protected fun readSettings(): GeneratorSettings = readSettingsFromResource("/settings/generator-test.settings.json")

    protected fun readSettingsFromResource(path: String): GeneratorSettings {
        val json = Json(JsonConfiguration.Stable)
        return json.parse(GeneratorSettings.serializer(), readResource(path))
    }

    protected fun readResource(path: String): String {
        return this::class.java.getResource(path).readText()
    }

    protected fun assertGeneratedFileMatched(file: GeneratedFile, path: String) {
        val base = if (file.type == GeneratedFile.Type.Main) {
            "/generated/main"
        } else {
            "/generated/test"
        }

        assertEquals(
            readResource("$base/$path"),
            file.content
        )
    }

    protected fun <K, V> assertMapEquals(expected: Map<K, V>, actual: Map<K, V>, message: String? = null) {
        if (expected.size != actual.size) {
            fail(message ?: "Expected and actual not contains same amount of keys")
        }
        expected.forEach {
            assertEquals(expected[it.key], actual[it.key], message)
        }
    }
}