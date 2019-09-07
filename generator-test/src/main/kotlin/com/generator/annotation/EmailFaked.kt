package com.generator.annotation

import net.ntworld.foundation.Faked
import net.ntworld.foundation.FakedData

@Faked(type = FakedData.Internet.emailAddress)
annotation class EmailFaked
