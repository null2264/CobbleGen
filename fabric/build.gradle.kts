import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dependencies.*
import dependencies.minecraft as MC
import net.fabricmc.loom.task.RemapJarTask
import java.util.Locale
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("multiloader")
    id(GradlePlugin.Loom.id) version "1.14.10" apply false
    id(GradlePlugin.LegacyLoom.id) version "1.14.10" apply false
}

val mcVersion = ext["mcVersion"] as CGVer
val loaderName = "fabric"
val projectPlugin = if (mcVersion.code >= 260100) {
    GradlePlugin.Loom
} else {
    GradlePlugin.LegacyLoom
}

group = project.properties["maven_group"] as String

apply(plugin = projectPlugin.id)

fun GradlePlugin.configure(configuration: LoomGradleExtensionAPI.() -> Unit = {}) =
    project.the<LoomGradleExtensionAPI>()
        .apply {
            if (mcVersion.code >= 12105) {
                accessWidenerPath = project.file("src/main/resources/cobblegen.accesswidener")
            }

            runConfigs {
                named("client") {
                    runDir = "../run/client"
                    configName = "Fabric Client"
                    //vmArg("-Dnull2264.cobblegen.gametest=true")
                    ideConfigGenerated(true)
                }
                named("server") {
                    runDir = "../run/server"
                    configName = "Fabric Server"
                    //vmArg("-Dnull2264.cobblegen.gametest=true")
                    ideConfigGenerated(true)
                }
                register("gametest") {
                    server()
                    name("Server GameTest")

                    vmArg("-Dnull2264.cobblegen.gametest=true")
                    vmArg("-Dfabric-api.gametest")
                    vmArg(
                        "-Dfabric-api.gametest.report-file=${
                            rootProject.layout.buildDirectory.file("reports/junit.xml").get().getAsFile()
                        }"
                    )

                    runDir("../run/serverGameTest")
                    ideConfigGenerated(false) // Mostly for CI
                }
            }

            configuration()
        }

val loom = projectPlugin.configure()

//if (projectPlugin.isLegacy) loom.silentMojangMappingsLicense()

tasks.withType<net.fabricmc.loom.task.RunGameTask>().configureEach {
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
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
    val minecraft by configurations
    minecraft(MC.versioned(mcVersion))
    if (projectPlugin.isLegacy) {
        val mappings by configurations
        mappings(loom.officialMojangMappings())
    }

    dep("modImplementation", "net.fabricmc:fabric-loader:0.18.4")

    // Mainly for testing
    // Only use gametest API for 1.21.5+, because the full FAPI is causing crashes on dev env
    // REF: https://github.com/FabricMC/fabric/issues/4491
    if (mcVersion.code in 11606..12104) {
        dep("modLocalRuntime", fapi.versioned(mcVersion))
    } else {
        try {
            // FIXME: Is Resource Loader even needed?
            //modLocalRuntime(fapiResourceLoader.versioned(mcVersion))
            dep("modLocalRuntime", fapiGameTest.versioned(mcVersion))
        } catch (_: IllegalStateException) {
        }
    }

    if (mcVersion.code >= 260100) {
        // FIXME: For some reason mixin is missing?
        dep("compileOnly", "org.spongepowered:mixin:0.8.7")
    }

    // Bunch of dummy classes, just so that the mod compiles
    compileOnly(project(":stubs"))

    if (mcVersion.code > 11605) {
        // FIXME: RuntimeOnly
        // We just want their source code so we can mixin it
        // modCompileOnly("io.github.fabricators_of_create:Porting-Lib:${project.properties["port_lib_version_1_18_2"]}")
        // modCompileOnly("com.simibubi.create:create-fabric-${project.properties["minecraft_version_1_18_2"]}:${project.properties["create_version_1_18_2"]}")

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
        if (mcVersion.code in 12002..12104) {  // FIXME: Not sure why it's not included
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
            dep("modCompileOnly", jei(mcVersion, loaderName, common = false, api = false).versioned(CGVer.wildcard()))
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

if (projectPlugin.isLegacy) {
    tasks.withType<RemapJarTask> {
        val shadowJar by tasks.getting(ShadowJar::class)
        inputFile.set(shadowJar.archiveFile)
    }
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
