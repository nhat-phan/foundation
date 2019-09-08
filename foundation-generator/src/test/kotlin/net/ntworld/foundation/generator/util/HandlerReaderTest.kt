package net.ntworld.foundation.generator.util

import net.ntworld.foundation.generator.GeneratorTest
import net.ntworld.foundation.generator.TestSuite
import kotlin.test.Test

class HandlerReaderTest: TestSuite() {
    @Test
    fun `test CreateUserRequestHandler`() {
        val allSettings = readSettings().toMutable()
        val handler = allSettings.getRequestHandler(GeneratorTest.RequestHandler.CreateUserRequestHandler)!!
        HandlerReader.findPrimaryConstructor(handler)

    }

}