import com.example.LocalServiceBus
import com.example.make
import net.ntworld.foundation.*
import net.ntworld.foundation.mocking.CallFakeBuilder
import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.test.AbstractMockableServiceBus
import kotlin.reflect.KClass

interface TestMockRequest : Request<TestMockResponse> {
    val name: String

    companion object
}

interface TestMockResponse : Response {
    companion object
}

@Handler
class TestMockRequestHandler : RequestHandler<TestMockRequest, TestMockResponse> {
    override fun handle(request: TestMockRequest): TestMockResponse {
        println("Real")
        return TestMockResponse.make(null)
    }
}

class MockableServiceBus<T>(private val bus: T) : AbstractMockableServiceBus<T>(bus)
    where T : ServiceBus, T : LocalBusResolver<Request<*>, RequestHandler<*, *>> {
    override fun guessRequestKClassByInstance(instance: Request<*>): KClass<out Request<*>>? {
        return when (instance) {
            is TestMockRequest -> TestMockRequest::class
            else -> null
        }
    }

    // We have the list at built-time, so no worries
    infix fun whenProcessing(request: TestMockRequest.Companion): CallFakeBuilder.Start<TestMockResponse> {
        return whenProcessing(TestMockRequest::class)
    }

    infix fun shouldProcess(request: TestMockRequest.Companion): CalledWithBuilder.Start {
        return shouldProcess(TestMockRequest::class)
    }
}

fun main() {
    // Foundation should generate a InfrastructureProvider for testing, which is
    // auto wired just like the other local buses
    // Then it will be used like this:
    //
    // infrastructure = TestInfrastructure(domain-infrastructure)
    // infrastructure.serviceBus() whenReceive Request willReturn Response
    //
    // infrastructure.serviceBus().process(Request(...))
    // infrastructure.serviceBus() shouldReceive Request match {
    //
    // }
    val response = TestMockResponse.make(null)
    val bus = MockableServiceBus(LocalServiceBus())

    // bus whenProcessing TestMockRequest alwaysReturns response
    bus shouldProcess TestMockRequest exact 3

    println(bus.process(TestMockRequest.make("test")))
    println(bus.process(TestMockRequest.make("test")).getResponse() === response)
    println(bus.process(TestMockRequest.make("test")).getResponse() === response)

    bus.verifyAll()
}