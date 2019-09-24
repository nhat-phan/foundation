package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratedFile
import net.ntworld.foundation.generator.GeneratorOutput
import net.ntworld.foundation.generator.GeneratorSettings
import net.ntworld.foundation.generator.setting.HandlerSetting
import net.ntworld.foundation.generator.type.ClassInfo
import net.ntworld.foundation.generator.util.ContractReader

abstract class AbstractMockableBusGeneratorTestGenerator {
    protected abstract val abstractMockableBus: ClassName
    protected abstract val busType: TypeName
    protected abstract val busResolverInput: TypeName
    protected abstract val busResolverOutput: TypeName
    protected abstract val busInputName: String
    protected abstract val guessKClassFunctionName: String
    protected abstract val guessKClassOutTypeName: String

    protected abstract fun findTarget(settings: GeneratorSettings, namespace: String?): ClassInfo

    protected abstract fun getContracts(settings: GeneratorSettings): List<ClassInfo>

    protected abstract fun getHandlers(settings: GeneratorSettings): List<HandlerSetting>

    protected abstract fun findWhenProcessingReturnsType(contract: ClassInfo): TypeName

    protected abstract fun findShouldProcessReturnsType(contract: ClassInfo): TypeName

    fun generate(settings: GeneratorSettings, namespace: String? = null): GeneratedFile {
        val handlers = getHandlers(settings)
        val target = findTarget(settings, namespace)
        if (handlers.isEmpty()) {
            return GeneratedFile.makeEmptyMainFile(target)
        }

        val file = buildFile(settings, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return GeneratedFile.makeTestFile(target, stringBuffer.toString())
    }

    private fun buildFile(settings: GeneratorSettings, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)

        val type = buildType(settings, target)
        file.addType(type)

        return file.build()
    }

    private fun buildType(settings: GeneratorSettings, target: ClassInfo): TypeSpec {
        val genericT = TypeVariableName.invoke(
            "T",
            busType,
            Framework.LocalBusResolver.parameterizedBy(
                busResolverInput,
                busResolverOutput
            )
        )
        val type = TypeSpec
            .classBuilder(target.toClassName())
            .addTypeVariable(genericT)
            .superclass(
                abstractMockableBus.parameterizedBy(genericT)
            )
            .addSuperclassConstructorParameter("bus")
            .addProperty(
                PropertySpec.builder("bus", genericT)
                    .addModifiers(KModifier.PRIVATE)
                    .initializer("bus")
                    .build()
            )
            .primaryConstructor(
                FunSpec
                    .constructorBuilder()
                    .addParameter("bus", genericT)
                    .build()
            )

        val contracts = getContracts(settings)
        type.addFunction(buildGuessKClassFunction(contracts))
        val reader = ContractReader(settings.contracts, settings.fakedAnnotations, settings.fakedProperties)
        contracts.forEach {
            if (reader.hasCompanionObject(it.fullName())) {
                buildHelperFunctionsForContractHaveCompanion(type, it)
            }
        }

        return type.build()
    }

    private fun buildGuessKClassFunction(contracts: List<ClassInfo>): FunSpec {
        val kClass = ClassName(packageName = "kotlin.reflect", simpleName = "KClass").parameterizedBy(
            TypeVariableName.invoke(guessKClassOutTypeName)
        )
        val code = CodeBlock.builder()
        code.beginControlFlow("return when (instance)")
        contracts.forEach {
            code.add("is %T -> %T::class\n", it.toClassName(), it.toClassName())
        }
        code.add("else -> null\n")
        code.endControlFlow()

        return FunSpec.builder(guessKClassFunctionName)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("instance", busResolverInput)
            .returns(kClass.copy(nullable = true))
            .addCode(code.build())
            .build()
    }

    protected open fun buildHelperFunctionsForContractHaveCompanion(type: TypeSpec.Builder, contract: ClassInfo) {
        val whenProcessing = FunSpec.builder("whenProcessing")
            .addModifiers(KModifier.INFIX)
            .addAnnotation(Framework.TestDslMock)
            .returns(findWhenProcessingReturnsType(contract))
            .addParameter(busInputName, contract.toClassName().nestedClass("Companion"))
            .addCode("return whenProcessing(%T::class)\n", contract.toClassName())

        val shouldProcess = FunSpec.builder("shouldProcess")
            .addModifiers(KModifier.INFIX)
            .addAnnotation(Framework.TestDslVerify)
            .returns(findShouldProcessReturnsType(contract))
            .addParameter(busInputName, contract.toClassName().nestedClass("Companion"))
            .addCode("return shouldProcess(%T::class)\n", contract.toClassName())

        type.addFunction(whenProcessing.build())
        type.addFunction(shouldProcess.build())
    }
}