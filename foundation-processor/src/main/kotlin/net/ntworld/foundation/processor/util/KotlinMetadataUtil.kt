package net.ntworld.foundation.processor.util

import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.KmTypeProjection
import kotlinx.metadata.jvm.KotlinClassHeader
import javax.lang.model.element.Element

internal object KotlinMetadataUtil {
    fun findPotentialContractsInKmType(type: KmType): Set<String> {
        val result = mutableSetOf<String>()
        collectPotentialContracts(type, result)

        return result
    }

    fun findPotentialContractsInTypeArguments(arguments: MutableList<KmTypeProjection>): Set<String> {
        val result = mutableSetOf<String>()
        collectPotentialContractsInArguments(arguments, result)
        return result
    }

    fun getKotlinClassHeaderFromElement(element: Element): KotlinClassHeader? {
        val metadataAnnotation = element.getAnnotation(Metadata::class.java)
        if (null === metadataAnnotation) {
            return null
        }

        return KotlinClassHeader(
            data1 = metadataAnnotation.data1,
            data2 = metadataAnnotation.data2,
            bytecodeVersion = metadataAnnotation.bytecodeVersion,
            extraInt = metadataAnnotation.extraInt,
            extraString = metadataAnnotation.extraString,
            kind = metadataAnnotation.kind,
            metadataVersion = metadataAnnotation.metadataVersion,
            packageName = metadataAnnotation.packageName
        )
    }

    private fun collectPotentialContractsInArguments(
        arguments: MutableList<KmTypeProjection>,
        bucket: MutableSet<String>
    ) {
        arguments.forEach {
            val argumentType = it.type
            if (null !== argumentType) {
                collectPotentialContracts(argumentType, bucket)
            }
        }
    }

    private fun collectPotentialContracts(type: KmType, bucket: MutableSet<String>) {
        val kmClass = type.classifier as? KmClassifier.Class ?: return
        val typeName = kmClass.name.replace('/', '.')
        if (!typeName.startsWith("kotlin")) {
            bucket.add(typeName)
        }

        collectPotentialContractsInArguments(type.arguments, bucket)
    }
}