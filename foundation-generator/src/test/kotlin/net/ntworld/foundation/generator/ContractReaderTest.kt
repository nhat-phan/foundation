package net.ntworld.foundation.generator

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ContractReaderTest {
    internal class ExpectedProperty(
        val order: Int,
        val type: String,
        val fakedType: String = ""
    )

    @Test
    fun `testFindPropertyFor BasicTypesContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.basic-types.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        assertMatchExpectations(
            mapOf(
                "byte" to ExpectedProperty(order = 1, type = "kotlin.Byte"),
                "short" to ExpectedProperty(order = 2, type = "kotlin.Short"),
                "int" to ExpectedProperty(order = 3, type = "kotlin.Int"),
                "long" to ExpectedProperty(order = 4, type = "kotlin.Long"),
                "float" to ExpectedProperty(order = 5, type = "kotlin.Float"),
                "double" to ExpectedProperty(order = 6, type = "kotlin.Double"),
                "string" to ExpectedProperty(order = 7, type = "kotlin.String"),
                "char" to ExpectedProperty(order = 8, type = "kotlin.Char"),
                "boolean" to ExpectedProperty(order = 9, type = "kotlin.Boolean"),
                "byteNullable" to ExpectedProperty(order = 10, type = "kotlin.Byte?"),
                "shortNullable" to ExpectedProperty(order = 11, type = "kotlin.Short?"),
                "intNullable" to ExpectedProperty(order = 12, type = "kotlin.Int?"),
                "longNullable" to ExpectedProperty(order = 13, type = "kotlin.Long?"),
                "floatNullable" to ExpectedProperty(order = 14, type = "kotlin.Float?"),
                "doubleNullable" to ExpectedProperty(order = 15, type = "kotlin.Double?"),
                "stringNullable" to ExpectedProperty(order = 16, type = "kotlin.String?"),
                "charNullable" to ExpectedProperty(order = 17, type = "kotlin.Char?"),
                "booleanNullable" to ExpectedProperty(order = 18, type = "kotlin.Boolean?")
            ),
            reader.findPropertiesOfContract("com.example.contract.BasicTypeContract")
        )
    }

    @Test
    fun `testFindPropertyFor ListTypeContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.list-type.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        assertMatchExpectations(
            mapOf(
                "listByte" to ExpectedProperty(
                    order = 1,
                    type = "kotlin.collections.List<kotlin.Byte>"
                ),
                "listOfNullableByte" to ExpectedProperty(
                    order = 2,
                    type = "kotlin.collections.List<kotlin.Byte?>"
                ),
                "nullableListOfByte" to ExpectedProperty(
                    order = 3,
                    type = "kotlin.collections.List<kotlin.Byte>?"
                ),
                "nullableListOfNullableByte" to ExpectedProperty(
                    order = 4,
                    type = "kotlin.collections.List<kotlin.Byte?>?"
                ),
                "listString" to ExpectedProperty(
                    order = 5,
                    type = "kotlin.collections.List<kotlin.String>"
                ),
                "listOfNullableString" to ExpectedProperty(
                    order = 6,
                    type = "kotlin.collections.List<kotlin.String?>"
                ),
                "nullableListOfString" to ExpectedProperty(
                    order = 7,
                    type = "kotlin.collections.List<kotlin.String>?"
                ),
                "nullableListOfNullableString" to ExpectedProperty(
                    order = 8,
                    type = "kotlin.collections.List<kotlin.String?>?"
                )
            ),
            reader.findPropertiesOfContract("com.example.contract.ListTypeContract")
        )
    }

    @Test
    fun `testFindPropertyFor NoSupertypeContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.no-supertypes.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        assertMatchExpectations(
            mapOf(
                "zelda" to ExpectedProperty(
                    order = 1,
                    type = "kotlin.String",
                    fakedType = "zelda.character"
                ),
                "name" to ExpectedProperty(
                    order = 2,
                    type = "kotlin.String",
                    fakedType = "starTrek.character"
                ),
                "email" to ExpectedProperty(
                    order = 3,
                    type = "kotlin.String?",
                    fakedType = "internet.emailAddress"
                ),
                "list" to ExpectedProperty(
                    order = 4,
                    type = "kotlin.collections.List<kotlin.String>",
                    fakedType = ""
                ),
                "phones" to ExpectedProperty(
                    order = 5,
                    type = "kotlin.collections.List<kotlin.Int>",
                    fakedType = ""
                )
            ),
            reader.findPropertiesOfContract("com.example.contract.NoSupertypeContractCommand")
        )
    }

    @Test
    fun `testFindPropertyFor OneSupertypeContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.one-supertype.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        assertMatchExpectations(
            mapOf(
                "zelda" to ExpectedProperty(order = 1, type = "kotlin.String", fakedType = "zelda.character"),
                "firstName" to ExpectedProperty(order = 2, type = "kotlin.String", fakedType = "name.firstName"),
                "lastName" to ExpectedProperty(order = 3, type = "kotlin.String", fakedType = "name.lastName"),
                "email" to ExpectedProperty(order = 4, type = "kotlin.String", fakedType = "internet.emailAddress")
            ),
            reader.findPropertiesOfContract("com.example.contract.OneSupertypeContract")
        )
    }

    @Test
    fun `testFindPropertyFor OneSupertypeOverrideContract`() {
        val allSettings = readSettingsFromResource("/settings/contract.one-supertype-override.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        assertMatchExpectations(
            mapOf(
                "zelda" to ExpectedProperty(order = 1, type = "kotlin.String", fakedType = "zelda.character"),
                "email" to ExpectedProperty(order = 2, type = "kotlin.String", fakedType = "internet.emailAddress"),
                "firstName" to ExpectedProperty(order = 3, type = "kotlin.String", fakedType = "name.firstName"),
                "lastName" to ExpectedProperty(order = 4, type = "kotlin.String", fakedType = "name.lastName")
            ),
            reader.findPropertiesOfContract("com.example.contract.OneSupertypeOverrideContract")
        )
    }

    // TODO: Add multiple supertypes cases

    private fun assertMatchExpectations(
        expectations: Map<String, ExpectedProperty>?,
        properties: Map<String, ContractReader.Property>?
    ) {
        if (null !== expectations && null !== properties) {
            return assertPropertiesListMatchExpectations(expectations, properties)
        }

        if (null !== expectations && null === properties) {
            fail("Expect that properties should not null but properties is null")
        }

        if (null === expectations && null !== properties) {
            fail("Expect that properties should be null but properties is not null")
        }
    }

    private fun assertPropertiesListMatchExpectations(
        expectations: Map<String, ExpectedProperty>,
        properties: Map<String, ContractReader.Property>
    ) {
        properties.forEach {
            val expectation = expectations[it.key]
            if (null === expectation) {
                fail("Property ${it.key} is not in expectations list")
            }
            val actual = it.value
            if (expectation.order != actual.order) {
                fail("""Expect "order" of property "${actual.name}" should be "${expectation.order}" but actual value is "${actual.order}"""")
            }

            if (expectation.fakedType != actual.fakedType) {
                fail("""Expect "fakedType" of property "${actual.name}" should be "${expectation.fakedType}" but actual value is "${actual.fakedType}"""")
            }

            if (expectation.type != actual.type.toString()) {
                fail("""Expect "typeName" of property "${actual.name}" should be "${expectation.type}" but actual value is "${actual.type}"""")
            }
        }
    }

    private fun readSettingsFromResource(path: String): GeneratorSettings {
        val input = ContractReaderTest::class.java.getResource(path).readText()
        val json = Json(JsonConfiguration.Stable)
        return json.parse(GeneratorSettings.serializer(), input)
    }
}