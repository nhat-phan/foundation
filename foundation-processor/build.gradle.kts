val artifactGroup: String by project
val foundationVersion: String by project
val releasingProcessorVersion: String by project
val kotlinxMetadataJvmVersion: String by project
val kotlinPoetVersion: String by project
val guavaVersion: String by project
val truthVersion: String by project
val compileTestingVersion: String by project

plugins {
    `maven-publish`
}

group = artifactGroup
version = releasingProcessorVersion

repositories {
    jcenter()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.nhat-phan.foundation:foundation-jvm:$foundationVersion")
    implementation(project(":foundation-generator"))
    implementation(kotlin("stdlib"))
    compile("org.jetbrains.kotlinx:kotlinx-metadata-jvm:$kotlinxMetadataJvmVersion")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testCompile("com.google.guava:guava:$guavaVersion")
    testCompile("com.google.truth:truth:$truthVersion")
    testCompile("com.google.testing.compile:compile-testing:$compileTestingVersion")
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