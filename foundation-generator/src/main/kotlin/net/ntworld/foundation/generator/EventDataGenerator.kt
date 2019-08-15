package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.common.ClassInfo
import java.io.File
import javax.annotation.processing.Filer
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

object EventDataGenerator {
    private val map = Map::class.parameterizedBy(String::class, Any::class)

    fun generate(settings: EventDataGeneratorSettings, out: Appendable) {
        buildFile(settings).writeTo(out)
    }

    fun generate(settings: EventDataGeneratorSettings, out: Filer) {
        buildFile(settings).writeTo(out)
    }

    fun generate(settings: EventDataGeneratorSettings, out: File) {
        buildFile(settings).writeTo(out)
    }

    internal fun buildFile(settings: EventDataGeneratorSettings): FileSpec {
        val target = findTarget(settings)
        val file = FileSpec.builder(target.packageName, target.className)
        Framework.addFileHeader(file, this::class.qualifiedName)
        file.addType(buildClass(settings, target))

        return file.build()
    }

    internal fun buildClass(settings: EventDataGeneratorSettings, target: ClassInfo): TypeSpec {
        return TypeSpec.classBuilder(target.className)
            .addModifiers(KModifier.DATA)
            .primaryConstructor(buildPrimaryConstructor())
            .addProperties(buildProperties(settings.type, settings.variant))
            .addSuperinterface(Framework.EventData)
            .build()
    }

    internal fun buildProperties(type: String, variant: Int): List<PropertySpec> {
        return listOf(
            PropertySpec.builder("id", String::class, KModifier.OVERRIDE).initializer("id").build(),
            PropertySpec.builder("streamId", String::class, KModifier.OVERRIDE).initializer("streamId").build(),
            PropertySpec.builder("streamType", String::class, KModifier.OVERRIDE).initializer("streamType").build(),
            PropertySpec.builder("version", Int::class, KModifier.OVERRIDE).initializer("version").build(),
            PropertySpec.builder("data", map, KModifier.OVERRIDE).initializer("data").build(),
            PropertySpec.builder("metadata", map, KModifier.OVERRIDE).initializer("metadata").build(),

            PropertySpec.builder("type", String::class, KModifier.OVERRIDE)
                .initializer("%S", type)
                .build(),
            PropertySpec.builder("variant", Int::class, KModifier.OVERRIDE)
                .initializer("%L", variant)
                .build()

        )
    }

    internal fun buildPrimaryConstructor(): FunSpec {
        return FunSpec.constructorBuilder()
            .addParameter("id", String::class)
            .addParameter("streamId", String::class)
            .addParameter("streamType", String::class)
            .addParameter("version", Int::class)
            .addParameter("data", map)
            .addParameter("metadata", map)
            .build()
    }

    internal fun findTarget(settings: EventDataGeneratorSettings): ClassInfo {
        if (null == settings.target) {
            return ClassInfo(
                className = "${settings.event.className}Data",
                packageName = "${settings.event.packageName}.generated"
            )
        }
        return settings.target
    }

}
