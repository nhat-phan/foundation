package net.ntworld.foundation.eventSourcing

import net.ntworld.foundation.Infrastructure

object EventConverterUtility {
    internal data class EncryptorInfo(
        val cipherId: String,
        val algorithm: String
    )

    fun <T : Any> write(eventData: EventData, name: String, value: T): EventConverterUtility {
        (eventData.data as MutableMap<String, Any>)[name] = value

        return this
    }

    fun <T : Any> writeMetadata(eventData: EventData, name: String, value: T): EventConverterUtility {
        (eventData.metadata as MutableMap<String, Any>)[name] = value

        return this
    }

    fun <T : Any> encrypt(
        eventData: EventData,
        name: String,
        value: T,
        infrastructure: Infrastructure
    ): EventConverterUtility {
        val infoKey = encryptorInfoKey(name)
        val encryptor = infrastructure.root.encryptor()
        this.write(eventData, name, encryptor.encrypt(value))
        this.writeMetadata(eventData, infoKey, EncryptorInfo(encryptor.cipherId, encryptor.algorithm))

        return this
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> read(eventData: EventData, name: String): T {
        return eventData.data[name] as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> readMetadata(eventData: EventData, name: String): T {
        return eventData.metadata[name] as T
    }

    fun <T : Any> decrypt(eventData: EventData, name: String, fakedType: String?, infrastructure: Infrastructure): T {
        val infoKey = encryptorInfoKey(name)
        if (!eventData.metadata.containsKey(infoKey)) {
            return read(eventData, name)
        }

        val info: EncryptorInfo = this.readMetadata(eventData, infoKey)
        val encryptor = infrastructure.root.encryptor(info.cipherId, info.algorithm)
        return encryptor.decrypt(
            this.read(eventData, name)
        )
    }

    private fun encryptorInfoKey(name: String) = "$name@encryptor"
}