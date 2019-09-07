package com.generator.contract

import com.generator.annotation.EmailFaked
import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData

interface User {
    @Faked(FakedData.Name.fullName)
    val name: String

    @EmailFaked
    val email: String

    val phone: String
}