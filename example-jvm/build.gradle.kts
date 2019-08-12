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
}

dependencies {
    implementation(project(":foundation"))
    implementation(project(":foundation-processor"))

    kapt(project(":foundation-processor"))

    implementation(kotlin("stdlib"))
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
