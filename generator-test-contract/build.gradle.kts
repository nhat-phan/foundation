val artifactGroup: String by project

val testVersion: String = "0.3.4"
val testKaptVersion: String = "0.3.4.1"

group = "$artifactGroup.generator-test-contract"
version = testVersion

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.nhat-phan.foundation:foundation-jvm:$testVersion")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
