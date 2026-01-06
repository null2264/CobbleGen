pluginManagement {
    repositories {
        google()
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
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.spongepowered.gradle.vanilla") {
                // REF: https://repo.spongepowered.org/#browse/browse:maven-public:org%2Fspongepowered%2Fvanillagradle%2F0.2.1-SNAPSHOT
                useModule("org.spongepowered:vanillagradle:0.2.1-20240507.024226-82")
            }
        }
    }
    plugins {
        id("com.gradleup.shadow") version "8.3.5"
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    }
}

rootProject.name = "CobbleGen"

include(":mclib")
include(":stubs")
include(":cobblegen")  // TODO: Maybe split?
