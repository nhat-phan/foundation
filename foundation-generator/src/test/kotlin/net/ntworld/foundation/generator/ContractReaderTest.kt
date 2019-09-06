package net.ntworld.foundation.generator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ContractReaderTest : TestSuite() {
    internal class ExpectedProperty(
        val order: Int,
        val type: String,
        val hasBody: Boolean = false,
        val fakedType: String = ""
    )

    @Test
    fun testHasCompanionObject() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val exampleContractExpectations = mapOf(
            "com.example.contract.BasicTypeContract" to false,
            "com.example.contract.DefaultValueContract" to true,
            "com.example.contract.ListTypeContract" to true,
            "com.example.contract.NoSupertypeContractCommand" to true,
            "com.example.contract.OneSupertypeContract" to true,
            "com.example.contract.OneSupertypeOverrideContract" to true
        )
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        exampleContractExpectations.forEach { (name, expected) ->
            assertEquals(expected, reader.hasCompanionObject(name), "failed with $name")
        }
    }

    @Test
    fun `testFindPropertyFor BasicTypesContract`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
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
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
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
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
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
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
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
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
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

    @Test
    fun `testFindPropertyFor DefaultValueContract`() {
        val allSettings = readSettingsFromResource("/settings/com.example.settings.json")
        val reader = ContractReader(allSettings.contracts, allSettings.fakedAnnotations)
        assertMatchExpectations(
            mapOf(
                "message" to ExpectedProperty(order = 1, type = "kotlin.String"),
                "code" to ExpectedProperty(order = 2, type = "kotlin.Int", fakedType = "number.randomNumber"),
                "type" to ExpectedProperty(order = 3, type = "kotlin.String", hasBody = true)
            ),
            reader.findPropertiesOfContract("com.example.contract.DefaultValueContract")
        )
    }

    // Bookmark: Add new test case when adding new contract settings
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

            if (expectation.hasBody != actual.hasBody) {
                fail("""Expect "hasBody" of property "${actual.name}" should be "${expectation.hasBody}" but actual value is "${actual.hasBody}"""")
            }

            if (expectation.type != actual.type.toString()) {
                fail("""Expect "typeName" of property "${actual.name}" should be "${expectation.type}" but actual value is "${actual.type}"""")
            }
        }
    }
}