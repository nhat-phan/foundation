package net.ntworld.foundation.generator

object GeneratorTest {
    private const val NAMESPACE = "com.generator"

    fun namespace(type: String? = null): String {
        return if (null === type) NAMESPACE else "$NAMESPACE.$type"
    }

    fun classNameOnly(qualifiedName: String): String {
        return qualifiedName.split(".").last()
    }

    object Contract {
        private const val NS_CONTRACT = "contract"

        fun namespace(type: String? = null): String {
            return if (null === type) "$NAMESPACE.$NS_CONTRACT" else "$NAMESPACE.$NS_CONTRACT.$type"
        }

        // TODO: change to RequestHandler's style to reduce calls namespace everywhere
        const val BasicTypeContract = "BasicTypeContract"
        const val DefaultValueContract = "DefaultValueContract"
        const val ListTypeContract = "ListTypeContract"
        const val NoSupertypeContract = "NoSupertypeContract"
        const val OneSupertypeContract = "OneSupertypeContract"
        const val OneSupertypeOverrideContract = "OneSupertypeOverrideContract"
        const val CustomTypeContract = "CustomTypeContract"
        const val CustomTypeContractAddress = "CustomTypeContractAddress"
    }

    object RequestHandler {
        private const val NS_REQUEST_HANDLER = "requestHandler"

        private fun namespace(type: String? = null): String {
            return if (null === type) "$NAMESPACE.$NS_REQUEST_HANDLER" else "$NAMESPACE.$NS_REQUEST_HANDLER.$type"
        }

        val CreateUserRequestHandler = namespace("CreateUserRequestHandler")
        val GetUserByIdRequestHandler = namespace("GetUserByIdRequestHandler")
    }
}