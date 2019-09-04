package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.asTypeName
import net.ntworld.foundation.generator.ContractReader
import net.ntworld.foundation.generator.TestSuite
import net.ntworld.foundation.generator.setting.ContractSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.type.KotlinMetadata
import kotlin.test.Test
import kotlin.test.assertEquals

class ContractImplementationFactoryTestGeneratorTest : TestSuite() {
    @Test
    fun `testGenerate NoOptionalProperty`() {
        val setting = ContractSetting(
            contract = ClassInfo(packageName = "com.example.contract", className = "NoOptionalProperty"),
            metadata = fakedMetadata,
            supertypes = null,
            properties = mapOf()
        )
        val properties = mapOf(
            "id" to ContractReader.Property(name = "id", order = 1, type = type, fakedType = ""),
            "email" to ContractReader.Property(name = "email", order = 2, type = type, fakedType = ""),
            "firstName" to ContractReader.Property(name = "firstName", order = 3, type = type, fakedType = ""),
            "lastName" to ContractReader.Property(name = "lastName", order = 4, type = type, fakedType = "")
        )

        val result = ContractImplementationFactoryTestGenerator.generate(
            setting,
            properties,
            ClassInfo(
                packageName = "${setting.contract.packageName}.generated",
                className = "${setting.contract.className}Impl"
            )
        )

        assertGeneratedFileMatched(result, "ContractImplementationFactory/NoOptionalProperty.txt")
    }

    @Test
    fun `testGenerate SomeOptionalProperties`() {
        val setting = ContractSetting(
            contract = ClassInfo(packageName = "com.example.contract", className = "SomeOptionalProperties"),
            metadata = fakedMetadata,
            supertypes = null,
            properties = mapOf()
        )
        val properties = mapOf(
            "id" to ContractReader.Property(name = "id", order = 1, type = type, fakedType = ""),
            "email" to ContractReader.Property(name = "email", order = 2, type = type, fakedType = "fakedEmail"),
            "firstName" to ContractReader.Property(
                name = "firstName",
                order = 3,
                type = type,
                fakedType = "fakedFirstName"
            ),
            "lastName" to ContractReader.Property(name = "lastName", order = 4, type = type, fakedType = "")
        )

        val result = ContractImplementationFactoryTestGenerator.generate(
            setting,
            properties,
            ClassInfo(
                packageName = "${setting.contract.packageName}.generated",
                className = "${setting.contract.className}Impl"
            )
        )

        assertGeneratedFileMatched(result, "ContractImplementationFactory/SomeOptionalProperties.txt")
    }


    @Test
    fun `testGenerate AllOptionalProperties`() {
        val setting = ContractSetting(
            contract = ClassInfo(packageName = "com.example.contract", className = "AllOptionalProperties"),
            metadata = fakedMetadata,
            supertypes = null,
            properties = mapOf()
        )
        val properties = mapOf(
            "id" to ContractReader.Property(name = "id", order = 1, type = type, fakedType = "UUID"),
            "email" to ContractReader.Property(name = "email", order = 2, type = type, fakedType = "fakedEmail"),
            "firstName" to ContractReader.Property(
                name = "firstName",
                order = 3,
                type = type,
                fakedType = "fakedFirstName"
            ),
            "lastName" to ContractReader.Property(
                name = "lastName",
                order = 4,
                type = type,
                fakedType = "fakedLastName"
            )
        )

        val result = ContractImplementationFactoryTestGenerator.generate(
            setting,
            properties,
            ClassInfo(
                packageName = "${setting.contract.packageName}.generated",
                className = "${setting.contract.className}Impl"
            )
        )

        assertGeneratedFileMatched(result, "ContractImplementationFactory/AllOptionalProperties.txt")
    }

    private val type = String::class.asTypeName()
    private val fakedMetadata = KotlinMetadata(
        kind = null,
        packageName = null,
        metadataVersion = null,
        bytecodeVersion = null,
        data1 = null,
        data2 = null,
        extraString = null,
        extraInt = null
    )
}