import dependencies.NEOFORM
import net.neoforged.moddevgradle.dsl.ModDevExtension
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import net.neoforged.moddevgradle.legacyforge.dsl.LegacyForgeExtension
import dependencies.minecraft as MC

val mcVersion = ext["mcVersion"] as Int
val projectPlugin = extra["projectPlugin"] as GradlePlugin
if (projectPlugin.isLegacy) {
    apply(GradlePlugin.LegacyMDG.id)
} else {
    apply(GradlePlugin.MDG.id)
}

inline fun <reified T: ModDevExtension> GradlePlugin.configure(configuration: T.() -> Unit = {}) {
    project.the<T>()
        .apply {}
        .configuration()
}

if (projectPlugin.isLegacy) {
    projectPlugin.configure<LegacyForgeExtension> {
        mcpVersion = MC.version(mcVersion)
    }
} else {
    projectPlugin.configure<NeoForgeExtension> {
        neoFormVersion = NEOFORM.version(mcVersion)
    }
}

repositories {
    maven("https://repo.spongepowered.org/maven/")
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.7")
}

publishing {
    publications {
        create<MavenPublication>("mavenCommon") {
            artifactId = "MCLib"
            from(components["java"])
        }
    }

    repositories {
    }
}

tasks.jar {
    manifest {
        attributes["Contains-Sources"] = "java,class"
    }
}
