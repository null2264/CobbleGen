import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "2.0.21"
}

val javaVersion = 21

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("io.github.z4kn4fein:semver:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("net.benwoodworth.knbt:knbt:0.11.8")
}

java {
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
    }
}
