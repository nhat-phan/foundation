plugins {
    kotlin("jvm") version "1.3.31"
}

repositories {
    jcenter()
    mavenCentral()
}

group = "com.github.nhat-phan.foundation"
version = "0.0.1"

dependencies {
    compile(project(":foundation"))
    implementation(kotlin("stdlib"))
}

