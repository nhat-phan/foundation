package net.ntworld.foundation.annotation

import com.github.javafaker.Faker

fun main() {
    val faker = Faker()
    // Faker::class.members

    faker.name().firstName()

}