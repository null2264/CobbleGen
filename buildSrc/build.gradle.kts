import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "2.0.21"
}

val javaVersion = 21

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        name = "Fabric Maven"
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        name = "Quilt Maven"
        url = uri("https://maven.quiltmc.org/repository/release/")
    }
}

dependencies {
    implementation("io.github.z4kn4fein:semver:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("net.benwoodworth.knbt:knbt:0.11.8")

    //#region yalmm deps
    implementation("org.jetbrains:annotations:26.0.2")
    implementation("org.ow2.asm:asm:9.7.1")
    implementation("de.undercouch:gradle-download-task:5.6.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.quiltmc:quilt-json5:1.0.3")
    implementation("net.fabricmc:mapping-io:0.8.0")
    implementation("net.fabricmc:stitch:0.6.2")
    implementation("net.fabricmc:tiny-remapper:0.12.0")
    implementation("cuchaz:enigma:2.5.2")
    implementation("cuchaz:enigma-cli:2.5.2")
    implementation("org.vineflower:vineflower:1.10.1")
    //#endregion
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
