val artifactGroup: String by project
val artifactVersion: String by project

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
    compile(project(":foundation"))
    implementation(kotlin("stdlib"))
}

