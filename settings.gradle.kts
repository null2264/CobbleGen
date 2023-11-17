pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        // For early access to "unstable" features
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/null2264/architectury-loom")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_PAT")
            }
        }
        maven("https://maven.architectury.dev/")
        maven("https://jitpack.io/")
        maven("https://files.minecraftforge.net/maven/")
        maven("https://repo.essential.gg/repository/maven-public/")
        maven("https://maven.neoforged.net/releases")
        gradlePluginPortal()
    }
    plugins {
        id("com.github.johnrengelman.shadow") version("8.1.1")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.github.null2264.preprocess")
                useModule("com.github.null2264:Preprocessor:ba61239")
        }
    }
}

rootProject.name = "CobbleGen"
rootProject.buildFileName = "root.gradle.kts"

listOf(
    "1.18.2-fabric",
    "1.18.2-forge",
    "1.19.2-fabric",
    "1.19.2-forge",
    "1.19.4-fabric",
    "1.19.4-forge",
    "1.20.1-fabric",
    "1.20.1-forge",
    "1.20.2-fabric",
    "1.20.2-forge",
    "1.20.2-neoforge",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle"
    }
}