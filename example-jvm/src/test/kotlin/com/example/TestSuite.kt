package com.example

import com.example.testAsset.ContractFactory
import com.github.javafaker.Faker
import net.ntworld.foundation.util.JavaFakerWrapper

open class TestSuite {
    val contracts = ContractFactory(JavaFakerWrapper(Faker()))

}