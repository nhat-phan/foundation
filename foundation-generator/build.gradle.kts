import org.jetbrains.kotlin.cli.jvm.main

val artifactGroup: String by project
val artifactVersion: String by project
val kotlinPoetVersion: String by project

plugins {
    kotlin("jvm") version "1.3.31"
    id("kotlinx-serialization") version "1.3.31"
    `maven-publish`
}

group = "$artifactGroup"
version = "$artifactVersion"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("com.github.javafaker:javafaker:1.0.0")
    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("com.github.javafaker:javafaker:1.0.0")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("source")
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            url = uri("$buildDir/repo")
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}
