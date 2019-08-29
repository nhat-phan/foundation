package net.ntworld.foundation.test

expect object TestEnvironmentGenerator {
    fun generateContractFactory(packageName: String, className: String, contracts: Map<String, Contract<*>>): String
}
