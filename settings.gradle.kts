pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.deftu.xyz/releases")
        maven("https://files.minecraftforge.net/maven/")
        maven("https://repo.essential.gg/repository/maven-public")
        gradlePluginPortal()
    }
    plugins {
        id("com.github.johnrengelman.shadow") version("8.1.1")
    }
}

rootProject.name = "CobbleGen"
rootProject.buildFileName = "root.gradle.kts"

listOf(
    "1.18.2-fabric",
    "1.18.2-forge",
    "1.19-fabric",
    "1.19.1-fabric",
    "1.19.2-fabric",
    "1.19.3-fabric",
    "1.19.4-fabric",
    "1.20-fabric",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle"
    }
}
