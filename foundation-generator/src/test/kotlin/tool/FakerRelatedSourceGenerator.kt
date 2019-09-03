package tool

import com.github.javafaker.Faker
import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.Framework
import net.ntworld.foundation.generator.GeneratorOutput
import org.junit.Test
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility

class FakerRelatedSourceGenerator {
    private val BLACKLIST = arrayOf(
        "hashCode",
        "toString",
        "equals",
        "letterify",
        "numerify",
        "resolve",
        "regexify",
        "bothify",
        "number.digits",
        "number.numberBetween",
        "number.randomDouble",
        "date.future",
        "date.past",
        "date.between",
        "internet.userAgent",
        "random.nextInt",
        "address.zipCodeByState",
        "address.countyByZipCode",
        "options.nextElement",
        "lorem.fixedString",
        "lorem.paragraphs",
        "lorem.sentences",
        "expression",
        "faker",
        "instance"
    )

    data class Group(
        val name: String,
        val items: List<Item>
    )

    data class Item(
        val name: String,
        val group: String,
        val code: String
    )

    @Test
    fun runGeneratorOfFakedData() {
        // println(generateFakedDataFile(getAllGroups()))
    }

    @Test
    fun runGeneratorOfJavaFakerWrapperJvmClass() {
        val data = getAllGroups()
        val items = mutableListOf<Item>()
        data.forEach {
            items.addAll(it.items)
        }

        // println(generateJavaFakerWrapperJvmClass(items))
    }

    private fun generateJavaFakerWrapperJvmClass(items: List<Item>): FileSpec {
        val file = FileSpec.builder(Framework.namespace + ".util", Framework.FakerRelatedSource_JavaFakerWrapper_Jvm)
        GeneratorOutput.addToolHeader(file, this::class.qualifiedName!! + "@runGeneratorOfJavaFakerWrapperJvmClass")

        val codeBlock = CodeBlock.builder()
        codeBlock.beginControlFlow("return when(data)")
        items.forEach {
            codeBlock.indent()
            codeBlock.add("%S -> %L\n", "${it.group}.${it.name}", it.code)
            codeBlock.unindent()
        }
        codeBlock.indent()
        codeBlock.add("""else -> throw Exception("Cannot resolve faked type = '%Ldata'")%L""", "$", "\n")
        codeBlock.unindent()
        codeBlock.endControlFlow()

        val classSpec = TypeSpec.classBuilder(Framework.FakerRelatedSource_JavaFakerWrapper_Jvm)
        classSpec.primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("faker", Framework.JavaFaker)
                .build()
        )
        classSpec.addProperty(
            PropertySpec.builder("faker", Framework.JavaFaker)
                .addModifiers(KModifier.PRIVATE)
                .initializer("faker")
                .build()
        )
        classSpec.addFunction(
            FunSpec.builder("makeFakeData")
                .returns(Any::class)
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("data", String::class)
                .addCode(codeBlock.build())
                .build()
        )
        classSpec.addSuperinterface(Framework.Faker)

        file.addType(classSpec.build())
        return file.build()
    }

    private fun generateFakedDataFile(data: List<Group>): FileSpec {
        val file = FileSpec.builder(Framework.namespace, Framework.FakerRelatedSource_FakedData)
        GeneratorOutput.addToolHeader(file, this::class.qualifiedName!! + "@runGeneratorOfFakedData")

        val fakedTypeObjectSpec = TypeSpec.objectBuilder(Framework.FakerRelatedSource_FakedData)

        data.forEach {
            val groupObjectSpec = TypeSpec.objectBuilder(it.name.capitalize())
            it.items.forEach {
                groupObjectSpec.addProperty(
                    PropertySpec
                        .builder(it.name, String::class)
                        .addModifiers(KModifier.CONST)
                        .initializer("%S", "${it.group}.${it.name}")
                        .build()
                )
            }
            fakedTypeObjectSpec.addType(groupObjectSpec.build())
        }

        file.addType(fakedTypeObjectSpec.build())
        return file.build()
    }

    private fun getAllGroups(): List<Group> {
        val faker = Faker()
        return faker::class.members
            .filter {
                it.visibility == KVisibility.PUBLIC && !BLACKLIST.contains(it.name)
            }
            .map {
                Group(name = it.name, items = getAllItems(it.name, it.call(faker)!!::class))
            }
    }

    private fun getAllItems(group: String, kClass: KClass<*>): List<Item> {
        return kClass.members
            .filter {
                it.visibility == KVisibility.PUBLIC && !BLACKLIST.contains(it.name) && !BLACKLIST.contains("$group.${it.name}")
            }
            .map {
                it.name
            }
            .distinct()
            .map {
                Item(name = it, group = group, code = "faker.$group().${it}()")
            }
    }
}