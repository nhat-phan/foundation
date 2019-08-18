import org.jetbrains.kotlin.cli.jvm.main

val artifactGroup: String by project
val artifactVersion: String by project
val kotlinPoetVersion: String by project

plugins {
    kotlin("jvm") version "1.3.31"
    id("kotlinx-serialization") version "1.3.31"
}

group = "$artifactGroup"
version = "$artifactVersion"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("com.github.javafaker:javafaker:1.0.0")
    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("com.github.javafaker:javafaker:1.0.0")
}
