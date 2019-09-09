val artifactGroup: String by project
val foundationVersion: String by project
val processorVersion: String by project

plugins {
    idea
}

group = "$artifactGroup.generator-test-contract"
version = processorVersion

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.nhat-phan.foundation:foundation-jvm:$foundationVersion")

    kapt(project(":foundation-processor"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

idea {
    module {
        sourceDirs = sourceDirs + files(
            "build/generated/source/kapt/main",
            "build/generated/source/kaptKotlin/main"
        )

        generatedSourceDirs = generatedSourceDirs + files(
            "build/generated/source/kapt/main",
            "build/generated/source/kaptKotlin/main",
            "build/generated/source/kapt/test",
            "build/generated/source/kaptKotlin/test"
        )

        resourceDirs = resourceDirs + files(
            "build/generated/source/kaptKotlin/main/resources"
        )
    }
}