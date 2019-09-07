package net.ntworld.foundation.processor.util

import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.KmTypeProjection

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