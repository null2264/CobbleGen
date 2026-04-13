pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.aap.my.id/") // For early access to "unstable" features
        maven("https://maven.architectury.dev/")
        maven("https://jitpack.io/")
        maven("https://files.minecraftforge.net/maven/")
        maven("https://repo.essential.gg/repository/maven-public/")
        maven("https://maven.neoforged.net/releases")
        maven("https://repo.spongepowered.org/maven/")
        gradlePluginPortal()
    }
    val mcVer: String by settings
    val (major, minor, patch) = mcVer
        .split(".")
        .toMutableList()
        .apply { while (this.size < 3) this.add("") }
    val mcVersion: Int = "${major}${minor.padStart(2, '0')}${patch.padStart(2, '0')}".toInt()

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.spongepowered.gradle.vanilla") {
                val targetModule = when (mcVersion) {
                    in 11605..12111 -> "org.spongepowered:vanillagradle:0.2.2-SNAPSHOT"
                    else -> "org.spongepowered:vanillagradle:0.3.0-SNAPSHOT"
                }
                useModule(targetModule)
            }
        }
    }
    plugins {
        id("com.gradleup.shadow") version "9.3.1"
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    }
}

rootProject.name = "CobbleGen"

include(":mclib")
include(":stubs")
include(":cobblegen")  // TODO: Maybe split?
