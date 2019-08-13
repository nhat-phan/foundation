package net.ntworld.foundation

import kotlin.reflect.KClass

open class InfrastructureContext(open val self: Infrastructure) : Infrastructure {
    @InfrastructureDsl
    override fun <A : Aggregate> factoryOf(type: KClass<A>): AggregateFactory<A> = self.factoryOf(type)

    @InfrastructureDsl
    override fun <A : Aggregate> storeOf(type: KClass<A>): AggregateStore<A> = self.storeOf(type)

    @InfrastructureDsl
    override fun <T : Any> idGeneratorOf(type: KClass<T>): IdGenerator = self.idGeneratorOf(type)

    @InfrastructureDsl
    inline fun <reified T : Aggregate> save(instance: T) {
        self.storeOf(T::class).save(instance)
    }
}
