package net.ntworld.foundation.processor

import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AggregateFactoryProcessorTest: TestSuite() {

    @Test
    /**
     * Demo code of this test can be found at: /com/test/aggregate/kotlin/TodoOneImpl.kt
     */
    fun `test @Implementation can be used with implementation of an Aggregate interface`() {
        val definedState = JavaFileObjects.forResource("com/test/aggregate/TodoState.java")
        val definedInterface = JavaFileObjects.forResource("com/test/aggregate/TodoOne.java")
        val implementation = JavaFileObjects.forResource("com/test/aggregate/TodoOneImpl.java")

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(
                listOf(
                    definedState, definedInterface, implementation
                )
            )
            .processedWith(FoundationProcessor())
            .compilesWithoutError()

        val settings = findAggregateFactorySettings("com.test.aggregate.TodoOneImpl")
        assertNotNull(settings)
        assertEquals("com.test.aggregate.TodoOneImpl", settings.implementation.fullName())
        assertEquals("com.test.aggregate.TodoOne", settings.aggregate.fullName())
        assertEquals("com.test.aggregate.TodoState", settings.state.fullName())
        assertEquals(true, settings.isAbstract)
        assertEquals(false, settings.isEventSourced)
    }

    @Test
    /**
     * Demo code of this test can be found at: /com/test/event/kotlin/UpdatedEvent.kt
     */
    fun `test @Implementation can be skipped if superinterface not from Aggregate`() {
        val definedInterface = JavaFileObjects.forResource("com/test/event/UpdatedEvent.java")
        val implementation = JavaFileObjects.forResource("com/test/event/UpdatedEventImpl.java")

        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(
                listOf(
                    definedInterface, implementation
                )
            )
            .processedWith(FoundationProcessor())
            .compilesWithoutError()

        assertNull(findAggregateFactorySettings("com.test.event.UpdatedEventImpl"))
        assertNull(findEventSettings("com.test.event.UpdatedEventImpl"))
    }
}