val artifactGroup: String by project
val artifactVersion: String by project
val kotlinPoetVersion: String by project

plugins {
    kotlin("jvm") version "1.3.31"
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
    implementation(project(":foundation", "jvmDefault"))
    implementation(project(":foundation-generator"))
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