package net.ntworld.foundation.processor

import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory
import net.ntworld.foundation.generator.type.EventField
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EventProcessorTest : TestSuite() {
//    @Test
//    fun `test @EventSourcing with data class`() {
//        val createdEvent = JavaFileObjects.forResource("com/test/event/CreatedEvent.java")
//
//        Truth.assert_()
//            .about(JavaSourcesSubjectFactory.javaSources())
//            .that(listOf(createdEvent))
//            .processedWith(EventProcessor())
//            .compilesWithoutError()
//
//        val settings = findEventSettings("com.test.event.CreatedEvent")
//        assertNotNull(settings)
//        assertEquals("com.test.event.CreatedEvent", settings.event.fullName())
//        assertEquals("com.test.event.CreatedEvent", settings.implementation.fullName())
//        assertEquals("created", settings.type)
//        assertEquals(0, settings.variant)
//        for (field in settings.fields) {
//            when (field.name) {
//                "id" -> assertEquals(EventField(name = "id", metadata = false, encrypted = false, faked = ""), field)
//                "companyId" -> assertEquals(
//                    EventField(
//                        name = "companyId",
//                        metadata = true,
//                        encrypted = false,
//                        faked = ""
//                    ), field
//                )
//                "email" -> assertEquals(
//                    EventField(name = "email", metadata = false, encrypted = true, faked = ""),
//                    field
//                )
//                "name" -> assertEquals(
//                    EventField(
//                        name = "name",
//                        metadata = false,
//                        encrypted = true,
//                        faked = "name.fullName"
//                    ), field
//                )
//                "time" -> assertEquals(EventField(name = "time", metadata = true, encrypted = false, faked = ""), field)
//            }
//        }
//    }
}