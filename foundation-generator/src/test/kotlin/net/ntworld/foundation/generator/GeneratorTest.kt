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

        val BasicTypeContract = namespace("BasicTypeContract")
        val DefaultValueContract = namespace("DefaultValueContract")
        val ListTypeContract = namespace("ListTypeContract")
        val NoSupertypeContract = namespace("NoSupertypeContract")
        val OneSupertypeContract = namespace("OneSupertypeContract")
        val OneSupertypeOverrideContract = namespace("OneSupertypeOverrideContract")
        val CustomTypeContract = namespace("CustomTypeContract")
        val CustomTypeContractAddress = namespace("CustomTypeContractAddress")
        val CreateAccountCommand = namespace("CreateAccountCommand")
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