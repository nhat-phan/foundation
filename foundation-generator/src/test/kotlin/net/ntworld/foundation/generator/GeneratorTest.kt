package net.ntworld.foundation.generator

object GeneratorTest {
    private const val NAMESPACE = "com.generator"

    fun namespace(type: String? = null): String {
        return if (null === type) NAMESPACE else "$NAMESPACE.$type"
    }

    object Contract {
        private const val NS_CONTRACT = "contract"

        fun namespace(type: String? = null): String {
            return if (null === type) "$NAMESPACE.$NS_CONTRACT" else "$NAMESPACE.$NS_CONTRACT.$type"
        }

        const val BasicTypeContract = "BasicTypeContract"
        const val DefaultValueContract = "DefaultValueContract"
        const val ListTypeContract = "ListTypeContract"
        const val NoSupertypeContract = "NoSupertypeContract"
        const val OneSupertypeContract = "OneSupertypeContract"
        const val OneSupertypeOverrideContract = "OneSupertypeOverrideContract"
        const val CustomTypeContract = "CustomTypeContract"
        const val CustomTypeContractAddress = "CustomTypeContractAddress"
    }
}