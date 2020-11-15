import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}
group = "me.kt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    testImplementation(kotlin("test-junit5"))
    implementation("no.tornado:tornadofx:1.7.20")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
}