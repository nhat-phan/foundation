val artifactGroup: String by project
val artifactVersion: String by project
val kotlinPoetVersion: String by project

plugins {
    kotlin("jvm") version "1.3.31"
}

group = "$artifactGroup"
version = "$artifactVersion"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    // implementation(project(path = ":foundation", configuration = "jvmDefault"))
    // compile(project(path = ":foundation", configuration = "default"))

    implementation(kotlin("stdlib"))
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("com.github.javafaker:javafaker:1.0.0")
}

