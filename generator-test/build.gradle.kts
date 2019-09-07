val artifactGroup: String by project
val artifactVersion: String by project
val kotlinxCoroutinesVersion: String by project
val kotlinxSerializationRuntimeVersion: String by project
val javaFakerVersion: String by project

plugins {
    kotlin("jvm") version "1.3.31"
    kotlin("kapt") version "1.3.31"
    id("kotlinx-serialization") version "1.3.31"
    idea
}

group = "$artifactGroup.integration-test"
version = artifactVersion

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":foundation"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$kotlinxSerializationRuntimeVersion")
    compile("com.github.javafaker:javafaker:$javaFakerVersion")

    kapt(project(":foundation-processor"))
    kaptTest(project(":foundation-processor"))

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
