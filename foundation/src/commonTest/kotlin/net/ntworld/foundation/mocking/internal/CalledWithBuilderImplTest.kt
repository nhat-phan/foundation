package net.ntworld.foundation.mocking.internal

import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.ParameterList
import kotlin.test.*

class CalledWithBuilderImplTest {
    private class Callable(private val verify: ((ParameterList, InvokeData) -> Boolean)?) {
        operator fun invoke(ordinal: Int, vararg params: Any): Boolean {
            assertNotNull(verify)

            return verify.invoke(ParameterList(params.toList()), InvokeData(ordinal))
        }
    }

    private fun runTest(syntax: (builder: CalledWithBuilderImpl) -> Unit): Callable {
        val builder = CalledWithBuilderImpl()

        syntax.invoke(builder)

        return Callable(builder.toCalledWith())
    }

    private val globalVerifier: (ParameterList, InvokeData) -> Boolean = { params, _ ->
        val (a, b) = params

        a == "global-a" && b == "global-b"
    }

    private val verifier: (ParameterList) -> Boolean = { params ->
        val (a, b) = params

        a == "a" && b == "b"
    }

    @Test
    fun `test atLeast`() {
        val builder = CalledWithBuilderImpl()
        builder atLeast 10

        assertEquals(10, builder.getCalledAtLeast())
        assertEquals(-1, builder.getCalledCount())
    }

    @Test
    fun `test exact`() {
        val builder = CalledWithBuilderImpl()
        builder exact  10

        assertEquals(-1, builder.getCalledAtLeast())
        assertEquals(10, builder.getCalledCount())
    }

    @Test
    fun `test once`() {
        val builder = CalledWithBuilderImpl()
        builder.once()

        assertEquals(-1, builder.getCalledAtLeast())
        assertEquals(1, builder.getCalledCount())
    }

    @Test
    fun `test twice`() {
        val builder = CalledWithBuilderImpl()
        builder.twice()

        assertEquals(-1, builder.getCalledAtLeast())
        assertEquals(2, builder.getCalledCount())
    }

    @Test
    fun `test thrice`() {
        val builder = CalledWithBuilderImpl()
        builder.thrice()

        assertEquals(-1, builder.getCalledAtLeast())
        assertEquals(3, builder.getCalledCount())
    }

    @Test
    fun `test alwaysMatch`() {
        val callable = runTest {
            it alwaysMatch globalVerifier
        }

        assertTrue(callable(0, "global-a", "global-b"))
        assertTrue(callable(1, "global-a", "global-b"))
        assertFalse(callable(2, "failed", "failed"))
    }

    @Test
    fun `test otherwiseMatch`() {
        val callable = runTest {
            it onFirstCallMatch verifier otherwiseMatch globalVerifier
        }

        assertTrue(callable(0, "a", "b"))
        assertFalse(callable(0, "failed", "failed"))

        assertTrue(callable(1, "global-a", "global-b"))
        assertFalse(callable(1, "failed", "failed"))

        assertTrue(callable(2, "global-a", "global-b"))
        assertFalse(callable(2, "failed", "failed"))
    }

    @Test
    fun `test match + onCall`() {
        val callable = runTest {
            it onCall 1 match verifier otherwiseMatch globalVerifier
        }

        assertTrue(callable(0, "global-a", "global-b"))
        assertFalse(callable(0, "failed", "failed"))

        assertTrue(callable(1, "a", "b"))
        assertFalse(callable(1, "failed", "failed"))

        assertTrue(callable(2, "global-a", "global-b"))
        assertFalse(callable(2, "failed", "failed"))
    }

    @Test
    fun `test onSecondCallMatch`() {
        val callable = runTest {
            it onSecondCallMatch verifier otherwiseMatch globalVerifier
        }

        assertTrue(callable(0, "global-a", "global-b"))
        assertFalse(callable(0, "failed", "failed"))

        assertTrue(callable(1, "a", "b"))
        assertFalse(callable(1, "failed", "failed"))

        assertTrue(callable(2, "global-a", "global-b"))
        assertFalse(callable(2, "failed", "failed"))
    }

    @Test
    fun `test onThirdCallMatch`() {
        val callable = runTest {
            it onThirdCallMatch verifier otherwiseMatch globalVerifier
        }

        assertTrue(callable(0, "global-a", "global-b"))
        assertFalse(callable(0, "failed", "failed"))

        assertTrue(callable(1, "global-a", "global-b"))
        assertFalse(callable(1, "failed", "failed"))

        assertTrue(callable(2, "a", "b"))
        assertFalse(callable(2, "failed", "failed"))
    }
}