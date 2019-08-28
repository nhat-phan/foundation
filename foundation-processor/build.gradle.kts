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
    maven("https://jitpack.io")
}

dependencies {
    // implementation(project(":foundation", "jvmDefault"))
    implementation(project(":foundation-generator"))
    implementation(kotlin("stdlib"))

    // We have a problem that if reference to the project :foundation
    // the generated pom is pointed to wrong artifact. In fact, the processor
    // can work with any version of foundation therefore let include them
    // directly to dependency like this
    implementation("com.github.nhat-phan.foundation:foundation-jvm:0.2.0.beta-1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testCompile("com.google.guava:guava:28.0-jre")
    testCompile("com.google.truth:truth:1.0")
    testCompile("com.google.testing.compile:compile-testing:0.18")
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