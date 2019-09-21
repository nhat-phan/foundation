package net.ntworld.foundation.mocking.internal

import net.ntworld.foundation.fluency.fourthCall
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.ParameterList
import kotlin.reflect.KClass
import kotlin.test.*

class CallFakeBuilderImplTest {
    private class Callable<R>(private val callFake: ((ParameterList, InvokeData) -> R)?) {
        operator fun invoke(ordinal: Int): R {
            assertNotNull(callFake)

            return callFake.invoke(ParameterList(listOf()), InvokeData(ordinal))
        }
    }

    private fun <R> runTest(syntax: (builder: CallFakeBuilderImpl<R>) -> Unit): Callable<R> {
        val builder = CallFakeBuilderImpl<R>()

        syntax.invoke(builder)

        return Callable(builder.toCallFake())
    }

    private fun expectException(instance: Throwable, block: () -> Unit) {
        try {
            block.invoke()
            assertFalse(true, "Expect $instance but it is not thrown")
        } catch (exception: Throwable) {
            assertSame(exception, instance)
        }
    }

    private fun expectException(kClass: KClass<out Throwable>, block: () -> Unit) {
        try {
            block.invoke()
            assertFalse(true, "Expect exception $kClass but it is not thrown")
        } catch (exception: Throwable) {
            assertEquals(exception::class, kClass)
        }
    }

    @Test
    fun `test alwaysReturns()`() {
        val callable = runTest<Int> {
            it alwaysReturns 1
        }

        assertEquals(1, callable(0))
        assertEquals(1, callable(1))
        assertEquals(1, callable(2))
        assertEquals(1, callable(3))
        assertEquals(1, callable(4))
    }

    @Test
    fun `test alwaysThrows()`() {
        val error = Exception()
        val callable = runTest<Int> {
            it alwaysThrows error
        }

        expectException(error) { callable(0) }
        expectException(error) { callable(1) }
        expectException(error) { callable(2) }
        expectException(error) { callable(3) }
        expectException(error) { callable(4) }
    }

    @Test
    fun `test alwaysRuns()`() {
        val error = Exception()
        val callable = runTest<Int> {
            it alwaysRuns { _, _ -> 0 }
        }

        assertEquals(0, callable(0))
        assertEquals(0, callable(1))
        assertEquals(0, callable(2))
        assertEquals(0, callable(3))
        assertEquals(0, callable(4))
    }

    @Test
    fun `test otherwiseReturns()`() {
        val callable = runTest<Int> {
            it onCall 1 returns 1 otherwiseReturns 100
        }

        assertEquals(100, callable(0))
        assertEquals(1, callable(1))
        assertEquals(100, callable(2))
        assertEquals(100, callable(3))
        assertEquals(100, callable(4))
    }

    @Test
    fun `test otherwiseThrows()`() {
        val callable = runTest<Int> {
            it onCall 1 returns 1 otherwiseThrows Exception()
        }

        expectException(Exception::class) { callable(0) }
        assertEquals(1, callable(1))
        expectException(Exception::class) { callable(2) }
        expectException(Exception::class) { callable(3) }
        expectException(Exception::class) { callable(4) }
    }

    @Test
    fun `test otherwiseRuns()`() {
        val callable = runTest<Int> {
            it onCall 1 returns 1 otherwiseRuns { _, _ -> 100 }
        }

        assertEquals(100, callable(0))
        assertEquals(1, callable(1))
        assertEquals(100, callable(2))
        assertEquals(100, callable(3))
        assertEquals(100, callable(4))
    }

    @Test
    fun `test onCall()`() {
        val callable = runTest<Int> {
            it onFirstCallReturns 1 onCall 1 returns 2 onThirdCallReturns 3 on fourthCall runs { 4 } otherwiseThrows Exception()
        }

        assertEquals(1, callable(0))
        assertEquals(2, callable(1))
        assertEquals(3, callable(2))
        assertEquals(4, callable(3))
        expectException(Exception::class) { callable(4) }
    }
}