import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dependencies.*
import java.util.Locale
import net.neoforged.moddevgradle.dsl.ModDevExtension
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import net.neoforged.moddevgradle.legacyforge.dsl.LegacyForgeExtension
import net.neoforged.moddevgradle.legacyforge.dsl.MixinExtension
import net.neoforged.moddevgradle.legacyforge.dsl.ObfuscationExtension
import net.neoforged.moddevgradle.tasks.JarJar
import org.gradle.kotlin.dsl.configureEach

plugins {
    id("multiloader")
    id(GradlePlugin.MDG.id) version "2.0.140" apply false
    id(GradlePlugin.LegacyMDG.id) version "2.0.140" apply false
}

val mcVersion = ext["mcVersion"] as CGVer
val isNeo = mcVersion.code >= 12002
val loaderName = if (isNeo) "neoforge" else "forge"
val projectPlugin = if (mcVersion.code >= 12002) {
    GradlePlugin.MDG
} else {
    GradlePlugin.LegacyMDG
}

group = project.properties["maven_group"] as String

apply(plugin = projectPlugin.id)

inline fun <reified T: ModDevExtension> GradlePlugin.configure(configuration: T.() -> Unit = {}) {
    project.the<T>()
        .apply {
            // FIXME: AccessWidener to AccessTransformer
            validateAccessTransformers = mcVersion.code >= 12105

            runs {
//                configureEach {
//                    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
//                }
                create("client") {
                    client()
                    gameDirectory.set(rootProject.file("run/client"))
                    ideName.set((if (!isNeo) "Forge" else "NeoForge") + " Client")
                }
                create("server") {
                    server()
                    gameDirectory.set(rootProject.file("run/server"))
                    ideName.set((if (!isNeo) "Forge" else "NeoForge") + " Server")
                }
                create("gametest") {
                    type = "gameTestServer"
                    gameDirectory.set(rootProject.file("run/serverGameTest"))
                    ideName.set("")
                    jvmArgument("-Dnull2264.cobblegen.gametest=true")
                    systemProperty("$loaderName.enabledGameTestNamespaces", "cobblegen")
                    systemProperty("$loaderName.gameTestServer", "true")
                    systemProperty("$loaderName.enableGameTest", "true")
                }
            }

            mods {
                create("${base.archivesName.get()}") {
                    sourceSet(sourceSets.main.get())
                    sourceSet(project(":xplat").sourceSets.main.get())
                }
            }
        }
        .configuration()

    // Newer MC handle mixins from (neoforge.)mods.toml file
    if (projectPlugin.isLegacy) {
        project.the<MixinExtension>()
            .apply {
                add(sourceSets.main.get(), "cobblegen.mixins.refmap.json")
                config("cobblegen.mixins.json")
            }
    }
}

if (projectPlugin.isLegacy) projectPlugin.configure<LegacyForgeExtension> {
    version = lexForge.version(mcVersion)
} else projectPlugin.configure<NeoForgeExtension> {
    version = NEO.version(mcVersion)
}


fun DependencyHandlerScope.dep(configuration: String, dependency: String) {
    add(
        configuration.let {
            if (projectPlugin.isLegacy) {
                it
            } else {
                it.replace("mod", "").replaceFirstChar { c -> c.lowercase(Locale.getDefault()) }
            }
        },
        dependency,
    )
}

dependencies {
    if (projectPlugin.isLegacy) {
        // needed for legacy forge, for mapping stuff
        annotationProcessor("org.spongepowered:mixin:0.8.7:processor")
    }

    // Bunch of dummy classes, just so that the mod compiles
    compileOnly(project(":stubs"))

    if (mcVersion.code > 11605) {
        // FIXME: RuntimeOnly
        // We just want their source code so we can mixin it
        // modCompileOnly(createMod(isNeo).versioned(0)) { isTransitive = false }

        // <- EMI
        if (mcVersion.code <= 11802) {
            dep("modCompileOnly", emi(mcVersion, null, api = true).versioned(CGVer.wildcard()))
            if (project.properties["recipe_viewer"] == "emi")
                dep("modLocalRuntime", emi(mcVersion).versioned(CGVer.wildcard()))
        } else {
            if (mcVersion.code <= 12105) {
                dep("modCompileOnly", emi(mcVersion, loaderName, api = true).versioned(CGVer.wildcard()))
                if (project.properties["recipe_viewer"] == "emi")
                    dep("modLocalRuntime", emi(mcVersion, loaderName).versioned(CGVer.wildcard()))
            }
        }
        // EMI ->

        // <- REI
        // Use the full package instead of 'api-' for (neo)forge, since the 'api-' didn't include @REIPlugin*
        dep("modCompileOnly", rei(loaderName, true).versioned(mcVersion))
        if (mcVersion.code in 11802..12001) {  // FIXME: Not sure why it's not included
            dep("modCompileOnly", "me.shedaniel.cloth:basic-math:0.6.1")
            dep("modCompileOnly", "dev.architectury:architectury:11.1.13")
        }
        if (project.properties["recipe_viewer"] == "rei") {
            if (mcVersion.code == 11902)  // REI's stupid dep bug
                dep("modLocalRuntime", "dev.architectury:architectury-fabric:6.5.77")
            dep("modLocalRuntime", rei(loaderName).versioned(mcVersion))
        }
        // REI ->

        // <- JEI
        dep("modCompileOnly", jei(mcVersion, loaderName, common = true, api = true).versioned(CGVer.wildcard()))
        dep("modCompileOnly", jei(mcVersion, loaderName, common = false, api = true).versioned(CGVer.wildcard()))
        if (project.properties["recipe_viewer"] == "jei")
            dep("modLocalRuntime", jei(mcVersion, loaderName, common = false, api = false).versioned(CGVer.wildcard()))
        // JEI ->

        /* FIXME: Broken, somehow
        if (mcVersion == 11802 && isFabric) {
            modLocalRuntime("com.tterrag.registrate_fabric:Registrate:MC1.18.2-1.1.7")
            modLocalRuntime("io.github.fabricators_of_create:Porting-Lib:${project.port_lib_version_1_18_2}")
            modLocalRuntime("com.simibubi.create:create-fabric-${project.minecraft_version_1_18_2}:${project.create_version_1_18_2}")
        }
         */
    }
}

tasks.jar {
    manifest.attributes(mapOf(
        "Contains-Sources" to "java,class",
        "MixinConfigs" to "cobblegen.mixins.json",
    ))
}

if (projectPlugin.isLegacy) project.the<ObfuscationExtension>().apply {
    tasks.jar {
        finalizedBy(tasks.named("reobfJar"))
    }
    reobfuscate(tasks.named<ShadowJar>("shadowJar"), sourceSets.main.get())
} else {
    tasks.jar {
        archiveClassifier = "dev"
    }

    val productionJar by tasks.register<Zip>("productionJar") {
        dependsOn(tasks.jar)
        archiveClassifier = ""
        archiveExtension = "jar"
        destinationDirectory = layout.buildDirectory.dir("libs")
        from(zipTree(tasks.shadowJar.flatMap { it.archiveFile }))
        if (!projectPlugin.isLoom()) from(tasks.named<JarJar>("jarJar"))
    }

    tasks.assemble {
        dependsOn(productionJar)
    }
}

//val deleteResources by tasks.creating(Delete::class) {
//    delete(file("build/resources/main"))
//}

//tasks.processResources {
//    dependsOn(tasks.named("copyCommonLoaderResources"))
//}

//tasks.getting(RunGameTask::class) {
//    dependsOn(tasks.named("copyCommonLoaderResources"))
//    finalizedBy(deleteResources)
//}

//tasks.sourcesJar {
//    val commonSources = project(":common").tasks.sourcesJar.get()
//    dependsOn(commonSources)
//    from(commonSources.archiveFile.map { zipTree(it) })
//}
