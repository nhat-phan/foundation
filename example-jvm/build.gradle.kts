val artifactGroup: String by project
val artifactVersion: String by project

plugins {
    kotlin("jvm") version "1.3.31"
    kotlin("kapt") version "1.3.31"
    idea
}

group = "$artifactGroup.example-jvm"
version = "$artifactVersion"

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    // ------- Dependencies for development
    implementation(kotlin("stdlib"))
    implementation(project(":foundation"))

    kapt(project(":foundation-processor"))

    // ------- Dependencies for testing with published artifact
    // implementation(kotlin("stdlib"))
    // implementation("com.github.nhat-phan.foundation:foundation-jvm:$artifactVersion")

    // kapt("com.github.nhat-phan.foundation:foundation-processor:$artifactVersion")
}

idea {
    module {
        sourceDirs = sourceDirs + files(
            "build/generated/source/kapt/main",
            "build/generated/source/kaptKotlin/main"
        )

        generatedSourceDirs = generatedSourceDirs + files(
            "build/generated/source/kapt/main",
            "build/generated/source/kaptKotlin/main"
        )
    }
}
