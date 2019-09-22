import com.example.LocalCommandBus
import com.example.LocalQueryBus
import com.example.LocalServiceBus
import com.example.contract.CreateTodoCommand
import com.example.contract.GetAllTodoQuery
import com.example.contract.GetAllTodoQueryResult
import com.example.make
import net.ntworld.foundation.*
import net.ntworld.foundation.cqrs.*
import net.ntworld.foundation.fluency.firstCall
import net.ntworld.foundation.mocking.CallFakeBuilder
import net.ntworld.foundation.mocking.CalledWithBuilder
import net.ntworld.foundation.test.AbstractMockableCommandBus
import net.ntworld.foundation.test.AbstractMockableServiceBus
import net.ntworld.foundation.fluency.nothing
import net.ntworld.foundation.mocking.InvokeData
import net.ntworld.foundation.mocking.TestDsl
import net.ntworld.foundation.test.AbstractMockableQueryBus
import net.ntworld.foundation.test.ServiceBusCallFakeBuilder
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
    @TestDsl.Mock
    infix fun whenProcessing(request: TestMockRequest.Companion)
        : ServiceBusCallFakeBuilder.Start<TestMockRequest, TestMockResponse> {
        return whenProcessing(TestMockRequest::class)
    }

    @TestDsl.Verify
    infix fun shouldProcess(request: TestMockRequest.Companion): CalledWithBuilder.Start {
        return shouldProcess(TestMockRequest::class)
    }
}

class MockableCommandBus<T>(private val bus: T) : AbstractMockableCommandBus<T>(bus)
    where T : CommandBus, T : LocalBusResolver<Command, CommandHandler<*>> {

    override fun guessCommandKClassByInstance(instance: Command): KClass<out Command>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class MockableQueryBus<T>(private val bus: T) : AbstractMockableQueryBus<T>(bus)
    where T : QueryBus, T : LocalBusResolver<Query<*>, QueryHandler<*, *>> {

    override fun guessQueryKClassByInstance(instance: Query<*>): KClass<out Query<*>>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
    val serviceBus = MockableServiceBus(LocalServiceBus())

    // serviceBus whenProcessing TestMockRequest alwaysReturns response

    serviceBus whenProcessing TestMockRequest on firstCall returns response otherwiseReturns response
    serviceBus whenProcessing TestMockRequest alwaysRuns { request, _ ->
        serviceBus.originalBus.process(request).getResponse()
    }

    serviceBus shouldProcess TestMockRequest exact 3
    serviceBus shouldProcess TestMockRequest on firstCall match { true } otherwiseMatch { _, _ -> true }

    println(serviceBus.process(TestMockRequest.make("test")).getResponse() === response)
    println(serviceBus.process(TestMockRequest.make("test")).getResponse() === response)
    println(serviceBus.process(TestMockRequest.make("test")).getResponse() === response)

    serviceBus.verifyAll()
}

fun testCommandBus(infrastructure: Infrastructure) {
    val commandBus = MockableCommandBus(LocalCommandBus(infrastructure))

    commandBus whenProcessing CreateTodoCommand::class alwaysDoes nothing
    commandBus whenProcessing CreateTodoCommand::class onCall 1 does nothing otherwiseDoes nothing
    commandBus whenProcessing CreateTodoCommand::class on firstCall throws Exception() otherwiseRuns { command, _ ->
        commandBus.originalBus.process(command)
    }

    commandBus whenProcessing CreateTodoCommand::class alwaysRuns commandBus.originalProcess
}

fun testQueryBus(infrastructure: Infrastructure) {
    val queryBus = MockableQueryBus(LocalQueryBus())

    queryBus whenProcessing GetAllTodoQuery::class on firstCall throws Exception()
    queryBus whenProcessing GetAllTodoQuery::class alwaysRuns { query, _ ->
        TODO()
    }
}