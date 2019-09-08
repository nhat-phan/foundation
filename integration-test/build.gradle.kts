val artifactGroup: String by project
val foundationVersion: String by project
val kotlinxCoroutinesVersion: String by project
val kotlinxSerializationRuntimeVersion: String by project
val javaFakerVersion: String by project

val testVersion: String = "0.3.2"

plugins {
    kotlin("jvm") version "1.3.31"
    kotlin("kapt") version "1.3.31"
    id("kotlinx-serialization") version "1.3.31"
    idea
}

group = "$artifactGroup.integration-test"
version = foundationVersion

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$kotlinxSerializationRuntimeVersion")
    compile("com.github.javafaker:javafaker:$javaFakerVersion")

    implementation("com.github.nhat-phan.foundation:foundation-jvm:$testVersion")
    kapt("com.github.nhat-phan.foundation:foundation-processor:$testVersion")
    kaptTest("com.github.nhat-phan.foundation:foundation-processor:$testVersion")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

idea {
    module {
        sourceDirs = sourceDirs + files(
            "build/generated/source/kapt/main",
            "build/generated/source/kaptKotlin/main"
        )

        testSourceDirs = testSourceDirs + files(
            "build/generated/source/kapt/test",
            "build/generated/source/kaptKotlin/test"
        )

        generatedSourceDirs = generatedSourceDirs + files(
            "build/generated/source/kapt/main",
            "build/generated/source/kaptKotlin/main",
            "build/generated/source/kapt/test",
            "build/generated/source/kaptKotlin/test"
        )
    }
}
