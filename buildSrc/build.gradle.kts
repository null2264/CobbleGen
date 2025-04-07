plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "2.1.20"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("io.github.z4kn4fein:semver:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("net.benwoodworth.knbt:knbt:0.11.8")
}
