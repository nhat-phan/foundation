package net.ntworld.foundation.internal

import net.ntworld.foundation.Error
import net.ntworld.foundation.Response
import kotlin.test.*

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

    @Test
    fun `test ifError`() {
        val successResponse = DummyResponse(error = null, result = "")
        ServiceBusProcessResultImpl(successResponse).ifError {
            throw Exception("Should not reach this line")
        }

        val failedResponse = DummyResponse(error = DummyError("type", "message", 500), result = "")
        try {
            ServiceBusProcessResultImpl(failedResponse).ifError {
                throw Exception(it.message)
            }
        } catch (exception: Exception) {
            assertEquals("message", exception.message)
            return
        }
        throw Exception("Should throw exception and not reach this line")
    }
}