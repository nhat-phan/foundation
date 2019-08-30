package net.ntworld.foundation.generator

import com.squareup.kotlinpoet.*
import net.ntworld.foundation.generator.type.ClassInfo
import kotlinx.serialization.Serializable
import net.ntworld.foundation.generator.setting.EventSourcingSetting

object EventEntityGenerator {

    fun generate(setting: EventSourcingSetting): GeneratedFile {
        val target = Utility.findEventEntityTarget(setting)
        val file = buildFile(setting, target)
        val stringBuffer = StringBuffer()
        file.writeTo(stringBuffer)

        return Utility.buildGeneratedFile(target, stringBuffer.toString())
    }

    internal fun buildFile(setting: EventSourcingSetting, target: ClassInfo): FileSpec {
        val file = FileSpec.builder(target.packageName, target.className)
        GeneratorOutput.addHeader(file, this::class.qualifiedName)
        file.addType(buildClass(setting, target))

        return file.build()
    }

    internal fun buildClass(setting: EventSourcingSetting, target: ClassInfo): TypeSpec {
        return TypeSpec.classBuilder(target.className)
            .addAnnotation(
                AnnotationSpec.builder(Serializable::class)
                    .build()
            )
            .addModifiers(KModifier.DATA)
            .primaryConstructor(buildPrimaryConstructor())
            .addProperties(buildProperties(setting.type, setting.variant))
            .addSuperinterface(Framework.EventEntity)
            .build()
    }

    internal fun buildProperties(type: String, variant: Int): List<PropertySpec> {
        return listOf(
            PropertySpec.builder("id", String::class, KModifier.OVERRIDE).initializer("id").build(),
            PropertySpec.builder("streamId", String::class, KModifier.OVERRIDE).initializer("streamId").build(),
            PropertySpec.builder("streamType", String::class, KModifier.OVERRIDE).initializer("streamType").build(),
            PropertySpec.builder("version", Int::class, KModifier.OVERRIDE).initializer("version").build(),
            PropertySpec.builder("data", String::class, KModifier.OVERRIDE).initializer("data").build(),
            PropertySpec.builder("metadata", String::class, KModifier.OVERRIDE).initializer("metadata").build(),

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
            .addParameter("data", String::class)
            .addParameter("metadata", String::class)
            .build()
    }
}
