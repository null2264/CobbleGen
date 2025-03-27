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
        id("com.gradleup.shadow") version("8.3.5")
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    }
}

rootProject.name = "CobbleGen"

extra.set("loom.platform", extra["loaderName"] as? String ?: "fabric")

include(":stubs")
