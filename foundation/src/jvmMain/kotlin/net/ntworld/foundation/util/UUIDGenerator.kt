package net.ntworld.foundation.util

import com.fasterxml.uuid.Generators
import net.ntworld.foundation.IdGenerator

object UUIDGenerator : IdGenerator {
    override fun generate(): String {
        return Generators.timeBasedGenerator().generate().toString()
    }
}
