import com.example.LocalServiceBus
import com.example.make
import generated.TestMockRequestImpl
import net.ntworld.foundation.*
import net.ntworld.foundation.mocking.ManualMock
import net.ntworld.foundation.mocking.MockBuilder
import net.ntworld.foundation.mocking.VerifyBuilder
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

class RequestHandlerManualMock<in T, out R : Response>(
    private val origin: RequestHandler<T, R>?
) : ManualMock(), RequestHandler<T, R> where T : Request<out R> {

    override fun handle(request: T): R {
        if (null === origin) {
            return mockFunction(this::handle, request)
        }

        return mockFunction(this::handle, { origin.handle(request) }, request)
    }

}

abstract class AbstractMockable<T: Contract> {
    // abstract fun guessKClass(instance: T): KClass<*>

    fun whenFunctionCalled(name: String) {
    }

    fun expectFunctionCalled(name: String) {
    }
}

class MockableServiceBus<T>(
    private val bus: T
) : ServiceBus, LocalBusResolver<Request<*>, RequestHandler<*, *>>
    where T : ServiceBus, T : LocalBusResolver<Request<*>, RequestHandler<*, *>> {
    private val mockedRequests = mutableMapOf<KClass<*>, MockBuilder.() -> Unit>()
    private val mockedInstancesByRequest = mutableMapOf<KClass<*>, RequestHandlerManualMock<*, *>>()

    override fun <R : Response> process(request: Request<R>): ServiceBusProcessResult<R> {
        // check request should be mocked or not
        return bus.process(request)
    }

    override fun resolve(instance: Request<*>): RequestHandler<*, *>? {
        val origin = bus.resolve(instance)
        val mockBuilder = mockedRequests[instance::class]
        if (null === mockBuilder) {
            return origin
        }

        val mockInstance = resolveMockInstance(instance::class, origin)
        net.ntworld.foundation.mocking.mock(mockInstance, mockBuilder)
        return mockInstance
    }

    private fun resolveMockInstance(
        request: KClass<out Request<*>>,
        origin: RequestHandler<*, *>?
    ): RequestHandlerManualMock<*, *> {
        val instance = mockedInstancesByRequest[request::class]
        if (null === instance) {
            mockedInstancesByRequest[request::class] = RequestHandlerManualMock(origin)
        }
        return mockedInstancesByRequest[request::class] as RequestHandlerManualMock
    }

    fun mock(request: KClass<Request<*>>, block: MockBuilder.() -> Unit) {
        mockedRequests[request] = block
    }

    fun <T : Request<*>, R : Response> willReturn(request: KClass<T>, response: R) {
        val block: MockBuilder.() -> Unit = {
            RequestHandler<*, *>::handle willReturn response
        }
        mockedRequests[request] = block
    }

    // We have the list at built-time, so no worries
    infix fun whenReceive(request: TestMockRequest.Companion) = whenReceive(TestMockRequest::class)

    infix fun whenReceive(request: KClass<out Request<*>>) {
        println(request)
    }

    inline infix fun <reified T> shouldReceive(request: T) {
        println(request)
    }
//    data class DefinedRequest(private val kClass: KClass<out Request<*>>)
//
//    companion object {
//        val TestMockRequest = DefinedRequest(TestMockRequest::class)
//    }
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


    val mockedBus = MockableServiceBus(LocalServiceBus())

    // ... whenReceive TestMockRequest willReturn TestMockResponse.make(null)
    // ... whenReceive TestMockRequest on firstTime willReturn TestMockResponse.make(null)
    // ... shouldReceive TestMockRequest
    // ... shouldReceive TestMockRequest match { ... }
    // ... shouldReceiveOnce TestMockRequest
    // ... shouldReceiveTwice TestMockRequest

    // mockedBus.shouldReceive(TestMockRequest)

    // mockedBus.willReturn(TestMockRequestImpl::class, TestMockResponse.make(null))

    // mockedBus.process(TestMockRequest.make("test"))

    // mockedBus whenReceive TestMockRequest
}