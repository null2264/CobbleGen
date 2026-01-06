import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dependencies.*
import net.fabricmc.loom.task.RemapJarTask

plugins {
}

val mcVersion = ext["mcVersion"] as Int
val mcVersionStr = ext["mcVersionStr"] as String
val mcHotfix = ext["mcHotfix"] as Int
val loaderName = ext["loaderName"] as String
val isFabric = ext["isFabric"] as Boolean
val isForge = ext["isForge"] as Boolean
val isNeo = ext["isNeo"] as Boolean

group = project.properties["maven_group"] as String

loom {
    if (mcVersion >= 12105) {
        accessWidenerPath = project.file("src/main/resources/cobblegen.accesswidener")
    }

    runConfigs {
        named("client") {
            runDir = "../run/client"
            configName = (if (isFabric) "Fabric" else if (!isNeo) "Forge" else "NeoForge") + " Client"
            //vmArg("-Dnull2264.cobblegen.gametest=true")
            ideConfigGenerated(true)
        }
        named("server") {
            runDir = "../run/server"
            configName = (if (isFabric) "Fabric" else if (!isNeo) "Forge" else "NeoForge") + " Server"
            //vmArg("-Dnull2264.cobblegen.gametest=true")
            ideConfigGenerated(true)
        }
        register("gametest") {
            server()
            name("Server GameTest")

            vmArg("-Dnull2264.cobblegen.gametest=true")
            vmArg("-Dfabric-api.gametest")
            vmArg("-Dfabric-api.gametest.report-file=${rootProject.layout.buildDirectory.file("reports/junit.xml").get().getAsFile()}")
            // For Forge-alike
            property("$loaderName.enabledGameTestNamespaces", "cobblegen")
            property("$loaderName.gameTestServer", "true")
            property("$loaderName.enableGameTest", "true")

            runDir("../run/serverGameTest")
            ideConfigGenerated(false) // Mostly for CI
            if (isNeo) {
                /*
                 * Apparently this replicate NeoGradle's
                 *
                 * runs {
                 *   gameTest {
                 *     type = "gameTestServer"
                 *   }
                 * }
                 */
                environment("gameTestServer")
                forgeTemplate("gameTestServer")
            }
        }
    }

    if (!isFabric && !isNeo) {
        forge {
            mixinConfigs = listOf(
                "cobblegen.mixins.json"
            )
        }
    }
}

dependencies {
    if (isFabric) {
        modImplementation("net.fabricmc:fabric-loader:0.17.2")

        // Mainly for testing
        // Only use gametest API for 1.21.5+, because the full FAPI is causing crashes on dev env
        // REF: https://github.com/FabricMC/fabric/issues/4491
        if (mcVersion in 11606..12104) {
            modLocalRuntime(fapi.versioned(mcVersion, mcHotfix))
        } else if (mcVersion in 12105..12110) {
            // FIXME: Is Resource Loader even needed?
            //modLocalRuntime(fapiResourceLoader.versioned(mcVersion, mcHotfix))
            modLocalRuntime(fapiGameTest.versioned(mcVersion, mcHotfix))
        }
    } else {
        if (!isNeo) {
            "forge"(lexForge.versioned(mcVersion, mcHotfix))
        } else {
            "neoForge"(neoForge.versioned(mcVersion, mcHotfix))
        }
    }

    if (mcVersion > 11605) {
        // TODO: Maybe it's no longer needed since we can just use ':stubs'?
        // We just want their source code so we can mixin it
        if (isFabric) {
            // modCompileOnly("io.github.fabricators_of_create:Porting-Lib:${project.properties["port_lib_version_1_18_2"]}")
            // modCompileOnly("com.simibubi.create:create-fabric-${project.properties["minecraft_version_1_18_2"]}:${project.properties["create_version_1_18_2"]}")
        } else {
            // modCompileOnly(createMod(isNeo).versioned(0)) { isTransitive = false }
        }

        // <- EMI
        if (mcVersion <= 11802) {
            modCompileOnly(emi(mcVersion, null, api = true).versioned(0, 0))
            if (project.properties["recipe_viewer"] == "emi" && isFabric)
                modLocalRuntime(emi(mcVersion).versioned(0, 0))
        } else {
            modCompileOnly(emi(mcVersion, loaderName, api = true).versioned(0, 0))
            if (project.properties["recipe_viewer"] == "emi")
                modLocalRuntime(emi(mcVersion, loaderName).versioned(0, 0))
        }
        // EMI ->

        // <- REI
        // Use the full package instead of 'api-' for (neo)forge, since the 'api-' didn't include @REIPlugin*
        modCompileOnly(rei(loaderName, true).versioned(mcVersion, mcHotfix))
        if (mcVersion in 12002..12104) {  // FIXME: Not sure why it's not included
            modCompileOnly("me.shedaniel.cloth:basic-math:0.6.1")
            modCompileOnly("dev.architectury:architectury:11.1.13")
        }
        if (project.properties["recipe_viewer"] == "rei") {
            if (mcVersion == 11902)  // REI's stupid dep bug
                modLocalRuntime("dev.architectury:architectury-fabric:6.5.77")
            modLocalRuntime(rei(loaderName).versioned(mcVersion, mcHotfix))
        }
        // REI ->

        // <- JEI
        modCompileOnly(jei(mcVersion, loaderName, common = true, api = true).versioned(0, 0))
        modCompileOnly(jei(mcVersion, loaderName, common = false, api = true).versioned(0, 0))
        if (project.properties["recipe_viewer"] == "jei")
            modCompileOnly(jei(mcVersion, loaderName, common = false, api = false).versioned(0, 0))
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

val remapJar by tasks.getting(RemapJarTask::class) {
    val shadowJar by tasks.getting(ShadowJar::class)
    dependsOn(shadowJar)
    if (isForge && mcVersion >= 12105) {
        atAccessWideners.add("cobblegen.accesswidener")
    }
    inputFile.set(shadowJar.archiveFile)
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
