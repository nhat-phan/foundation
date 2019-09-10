package net.ntworld.foundation.generator.test

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.*
import net.ntworld.foundation.generator.Utility
import net.ntworld.foundation.generator.type.ClassInfo

class UtilityTestGenerator(private val platform: Platform) {
    private val classInfos = mutableListOf<ClassInfo>()

    fun add(item: ClassInfo) {
        classInfos.add(item)
    }

    fun generate(namespace: String? = null): GeneratedFile {
        val target = Utility.findUtilityTargetForTest(
            classInfos,
            namespace
        )
        val file = buildFile(target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return GeneratedFile.makeTestFile(target, stringBuffer.toString())
    }

    private fun buildFile(target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(target))

        return file.build()
    }

    private fun buildClass(target: ClassInfo): TypeSpec {
        val type = TypeSpec.objectBuilder(target.toClassName())
        type.addProperty(buildCustomFakerProperty())
        type.addProperty(buildDefaultFakerProperty())
        type.addProperty(buildFakerProperty())
        type.addFunction(buildSetFakerFunction(target))

        return type.build()
    }

    private fun buildCustomFakerProperty(): PropertySpec {
        return PropertySpec.builder("customFaker", Framework.Faker.copy(nullable = true))
            .addModifiers(KModifier.PRIVATE)
            .mutable()
            .initializer("null")
            .build()
    }

    private fun buildDefaultFakerProperty(): PropertySpec {
        val code = when (platform) {
            Platform.Jvm -> getInitDefaultFakerForJvm()
        }

        return PropertySpec.builder("defaultFaker", Framework.Faker)
            .addModifiers(KModifier.PRIVATE)
            .initializer(code)
            .build()
    }

    private fun buildSetFakerFunction(target: ClassInfo): FunSpec {
        val func = FunSpec.builder("setFaker")
        func.addParameter("faker", Framework.Faker)
        func.returns(target.toClassName())

        func.addCode("this.customFaker = faker\n")
        func.addCode("return this\n")

        return func.build()
    }

    private fun buildFakerProperty(): PropertySpec {
        val getter = FunSpec.getterBuilder()
        getter.addCode("val currentCustomFaker = customFaker\n")
        getter.addCode("return if (null !== currentCustomFaker) currentCustomFaker else defaultFaker\n")

        return PropertySpec.builder("faker", Framework.Faker)
            .getter(getter.build())
            .build()
    }


    private fun getInitDefaultFakerForJvm(): CodeBlock {
        val code = CodeBlock.builder()
        code.add("%T(%T())", Framework.JavaFakerWrapper, Framework.JavaFaker)

        return code.build()
    }
}