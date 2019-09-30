package net.ntworld.foundation.internal

import net.ntworld.foundation.Error
import net.ntworld.foundation.Response
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ServiceBusProcessResultImplTest {
    data class DummyError(
        override val type: String,
        override val message: String,
        override val code: Int
    ): Error

    data class DummyResponse(
        override val error: Error?,
        val result: String
    ) : Response

    @Test
    fun `test hasError`() {
        val successResponse = DummyResponse(error = null, result = "")
        assertFalse(ServiceBusProcessResultImpl(successResponse).hasError())

        val failedResponse = DummyResponse(error = DummyError("type", "message", 500), result = "")
        assertTrue(ServiceBusProcessResultImpl(failedResponse).hasError())
    }

    @Test
    fun `test getResponse`() {
        val successResponse = DummyResponse(error = null, result = "")
        assertSame(successResponse, ServiceBusProcessResultImpl(successResponse).getResponse())
    }


}