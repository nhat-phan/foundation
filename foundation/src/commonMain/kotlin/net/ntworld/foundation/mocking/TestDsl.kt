package net.ntworld.foundation.mocking

interface TestDsl {
    @DslMarker
    annotation class Mock

    @DslMarker
    annotation class Verify
}