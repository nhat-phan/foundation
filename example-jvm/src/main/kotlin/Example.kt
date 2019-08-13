package test.xxx

// import Greeter
// import net.ntworld.foundation.annotation.Encrypted
// import net.ntworld.foundation.annotation.EventType
import net.ntworld.foundation.eventSourcing.Event
import kotlin.reflect.KClass


class Test : Event {
    val id: String = ""
}

interface Agg {
}

interface Factory<A : Agg> {
    fun generate(): A
}

interface Infra {
    fun <A : Agg> factoryOf(type: KClass<A>): Factory<A>
}

class Applicant : Agg {}
class Application : Agg {}
class ApplicantFactory : Factory<Applicant> {
    override fun generate(): Applicant {
        println("Applicant factory")
        return Applicant()
    }
}

interface FactoryResolver {
    fun<A: Agg> resolveFactory(type: KClass<A>): Factory<A>?
}
class FactoryResolverImpl: FactoryResolver {
    override fun<A: Agg> resolveFactory(type: KClass<A>): Factory<A>? {
        return resolveFactoryImpl(type)
    }

    private inline fun <reified T : Factory<A>, A: Agg> resolveFactoryImpl(type: KClass<A>): T? {
        return when (type) {
            Applicant::class -> ApplicantFactory() as T
            else -> null
        }
    }
}

class Infrastructure : Infra {
    private val resolvers: Iterable<FactoryResolver> = listOf(
        FactoryResolverImpl()
    )

    override fun <A : Agg> factoryOf(type: KClass<A>): Factory<A> {
        for (resolver in resolvers) {
            val factory = resolver.resolveFactory(type)
            if (null !== factory) {
                return factory
            }
        }
        throw Exception("Cannot resolve $type")
    }
}

fun main() {
    // println("x 2")
    // Greeter("test").greet()
    Infrastructure().factoryOf(Applicant::class).generate()
    Infrastructure().factoryOf(Application::class).generate()
}