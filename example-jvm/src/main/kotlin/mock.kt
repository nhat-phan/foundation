import com.example.make
import net.ntworld.foundation.*
import net.ntworld.foundation.mocking.ManualMock
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

class MockableServiceBus<T>(
    private val bus: T
) : ServiceBus, LocalBusResolver<Request<*>, RequestHandler<*, *>>
    where T : ServiceBus, T : LocalBusResolver<Request<*>, RequestHandler<*, *>> {
    private val mockedRequests = mutableMapOf<KClass<*>, Response?>()

    override fun <R : Response> process(request: Request<R>): ServiceBusProcessResult<R> {
        return bus.process(request)
    }

    override fun resolve(instance: Request<*>): RequestHandler<*, *>? {
        return bus.resolve(instance)
    }

    fun mock(request: KClass<Request<*>>) {
        this.mockedRequests[request] = null
    }

    fun <T : Response> mock(request: KClass<Request<T>>, response: T) {
        this.mockedRequests[request] = null
    }

    fun verify(request: KClass<Request<*>>) {

    }
}

fun client() {
    // Foundation should generate a InfrastructureProvider for testing, which is
    // auto wired just like the other local buses
    // Then it will be used like this:
    //
    // infrastructure = TestInfrastructure(domain-infrastructure)
    // infrastructure.serviceBus().mock(TestMockRequestHandler::class) {
    //   callFake {}
    // }
    //
    // infrastructure.serviceBus().process(Request(...))
    // infrastructure.serviceBus().verify(TestMockRequestHandler::class) {
    //   called true
    // }
}