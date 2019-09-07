package com.generator.contract

import com.generator.annotation.EmailFaked
import com.generator.annotation.NoAffectedFaked
import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData
import net.ntworld.foundation.cqrs.Command

interface NoSupertypeContract : Command {
    @get:Faked(type = FakedData.Zelda.character)
    val zelda: String

    @Faked(type = FakedData.StarTrek.character)
    val name: String

    @EmailFaked
    val email: String?

    @NoAffectedFaked
    val list: List<String>

    val phones: List<Int>

    companion object
}
