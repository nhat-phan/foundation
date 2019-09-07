package net.ntworld.foundation.generator.type

import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.serialization.Serializable
import javax.lang.model.element.Element

@Serializable
data class KotlinMetadata(
    val kind: Int?,
    val packageName: String?,
    val metadataVersion: List<Int>?,
    val bytecodeVersion: List<Int>?,
    val data1: List<String>?,
    val data2: List<String>?,
    val extraString: String?,
    val extraInt: Int?
) {
    fun isEmpty(): Boolean {
        return null === kind &&
            null === packageName &&
            null === metadataVersion &&
            null === bytecodeVersion &&
            null === data1 &&
            null === data2 &&
            null === extraString &&
            null === extraInt
    }

    fun isNotEmpty(): Boolean {
        return null !== kind ||
            null !== packageName ||
            null !== metadataVersion ||
            null !== bytecodeVersion ||
            null !== data1 ||
            null !== data2 ||
            null !== extraString ||
            null !== extraInt
    }

    companion object {
        fun empty(): KotlinMetadata {
            return KotlinMetadata(
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

        fun fromElement(element: Element): KotlinMetadata {
            val annotation = element.getAnnotation(Metadata::class.java)

            return KotlinMetadata(
                kind = annotation.kind,
                packageName = annotation.packageName,
                metadataVersion = annotation.metadataVersion.toList(),
                bytecodeVersion = annotation.bytecodeVersion.toList(),
                data1 = annotation.data1.toList(),
                data2 = annotation.data2.toList(),
                extraString = annotation.extraString,
                extraInt = annotation.extraInt
            )
        }

        fun fromKotlinClassHeader(header: KotlinClassHeader): KotlinMetadata {
            return KotlinMetadata(
                kind = header.kind,
                packageName = header.packageName,
                metadataVersion = header.metadataVersion.toList(),
                bytecodeVersion = header.bytecodeVersion.toList(),
                data1 = header.data1.toList(),
                data2 = header.data2.toList(),
                extraString = header.extraString,
                extraInt = header.extraInt
            )
        }

        fun toKotlinClassHeader(metadata: KotlinMetadata): KotlinClassHeader {
            var metadataVersion: IntArray? = null
            if (null !== metadata.metadataVersion) {
                metadataVersion = IntArray(metadata.metadataVersion.size) { 0 }
                metadata.metadataVersion.forEachIndexed { index, item ->
                    metadataVersion[index] = item
                }
            }

            var bytecodeVersion: IntArray? = null
            if (null !== metadata.bytecodeVersion) {
                bytecodeVersion = IntArray(metadata.bytecodeVersion.size) { 0 }
                metadata.bytecodeVersion.forEachIndexed { index, item ->
                    bytecodeVersion[index] = item
                }
            }

            var data1: Array<String>? = null
            if (null !== metadata.data1) {
                data1 = Array(metadata.data1.size) { "" }
                metadata.data1.forEachIndexed { index, item ->
                    data1[index] = item
                }
            }

            var data2: Array<String>? = null
            if (null !== metadata.data2) {
                data2 = Array(metadata.data2.size) { "" }
                metadata.data2.forEachIndexed { index, item ->
                    data2[index] = item
                }
            }

            return KotlinClassHeader(
                data1 = data1,
                data2 = data2,
                bytecodeVersion = bytecodeVersion,
                extraInt = metadata.extraInt,
                extraString = metadata.extraString,
                kind = metadata.kind,
                metadataVersion = metadataVersion,
                packageName = metadata.packageName
            )
        }
    }
}
