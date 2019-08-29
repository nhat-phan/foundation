package net.ntworld.foundation.test

expect object TestAssetsGenerator {
    fun generateContractFactory(packageName: String, className: String, contracts: Map<String, Contract<*>>): String
}
