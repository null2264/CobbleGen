pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.aap.my.id/") // For early access to "unstable" features
        maven("https://maven.architectury.dev/")
        maven("https://jitpack.io/")
        maven("https://files.minecraftforge.net/maven/")
        maven("https://repo.essential.gg/repository/maven-public/")
        maven("https://maven.neoforged.net/releases")
        gradlePluginPortal()
    }
    plugins {
        id("com.github.johnrengelman.shadow") version("8.1.1")
        id("io.github.null2264.preprocess") version("1.0-SNAPSHOT")
    }
}

rootProject.name = "CobbleGen"
rootProject.buildFileName = "root.gradle.kts"

listOf(
    "1.16.5-fabric",
    "1.16.5-forge",
    "1.18.2-fabric",
    "1.18.2-forge",
    "1.19.2-fabric",
    "1.19.2-forge",
    "1.20.1-fabric",
    "1.20.1-forge",
    "1.20.2-fabric",
    "1.20.2-neoforge",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle"
    }
}