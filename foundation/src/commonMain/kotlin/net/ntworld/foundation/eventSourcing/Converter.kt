package net.ntworld.foundation.eventSourcing

object Converter {
    internal data class EncryptorInfo(
        val cipherId: String,
        val algorithm: String
    )

    fun <T : Any> write(eventData: EventData, name: String, value: T): Converter {
        (eventData.data as MutableMap<String, Any>)[name] = value

        return this
    }

    fun <T : Any> writeMetadata(eventData: EventData, name: String, value: T): Converter {
        (eventData.metadata as MutableMap<String, Any>)[name] = value

        return this
    }

    fun <T : Any> encrypt(
        eventData: EventData,
        name: String,
        value: T,
        infrastructure: EventSourcingInfrastructure
    ): Converter {
        val infoKey = encryptorInfoKey(name)
        val encryptor = infrastructure.encryptor()
        this.write(eventData, name, encryptor.encrypt(value))
        this.writeMetadata(eventData, infoKey, EncryptorInfo(encryptor.cipherId, encryptor.algorithm))

        return this
    }

    fun <T : Any> read(eventData: EventData, name: String): T {
        return eventData.data[name] as T
    }

    fun <T : Any> readMetadata(eventData: EventData, name: String): T {
        return eventData.metadata[name] as T
    }

    fun <T : Any> decrypt(eventData: EventData, name: String, infrastructure: EventSourcingInfrastructure): T {
        val infoKey = encryptorInfoKey(name)
        if (!eventData.metadata.containsKey(infoKey)) {
            return read(eventData, name)
        }

        val info: EncryptorInfo = this.readMetadata(eventData, infoKey)
        val encryptor = infrastructure.encryptor(info.cipherId, info.algorithm)
        return encryptor.decrypt(
            this.read(eventData, name)
        )
    }

    private fun encryptorInfoKey(name: String) = "$name@encryptor"
}