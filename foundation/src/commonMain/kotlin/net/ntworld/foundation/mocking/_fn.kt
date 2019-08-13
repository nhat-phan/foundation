package net.ntworld.foundation.mocking

fun mock(instance: ManualMock, block: MockBuilder.() -> Unit) = MockBuilder(instance).apply(block)

fun verify(instance: ManualMock, block: VerifyBuilder.() -> Unit) = VerifyBuilder(instance).apply(block).verify()

fun reset(instance: ManualMock) = instance.resetAll()