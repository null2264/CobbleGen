import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dependencies.*
import net.fabricmc.loom.task.RemapJarTask

plugins {
}

val mcVersion = ext["mcVersion"] as Int
val mcVersionStr = ext["mcVersionStr"] as String
val loaderName = ext["loaderName"] as String
val isFabric = ext["isFabric"] as Boolean
val isForge = ext["isForge"] as Boolean
val isNeo = ext["isNeo"] as Boolean

// TODO: addingVersion - Add "-" suffix to support snapshots
val supportedVersionRange: List<String?> = mapOf(
        11605 to listOf(null, "1.16.5"),
        11802 to listOf(null, "1.18.2"),
        11902 to listOf("1.19-", "1.19.2"),
        11904 to listOf("1.19.3-", "1.19.4"),
        12001 to listOf("1.20-", "1.20.1"),
        12002 to listOf("1.20.2-", if (!isNeo) "1.20.4" else "1.20.3"),
        12004 to listOf(null, "1.20.4"),  // for Neo
        12006 to listOf("1.20.5-", "1.20.6"),
        12101 to listOf("1.21-", "1.21.1"),
        12103 to listOf("1.21.2-", null),
)[mcVersion] ?: listOf()

group = project.properties["maven_group"] as String

loom {
    silentMojangMappingsLicense()

    runConfigs {
        named("client") {
            runDir = "../run/client"
            configName = (if (isFabric) "Fabric" else if (!isNeo) "Forge" else "NeoForge") + "Client"
            ideConfigGenerated(true)
        }
        named("server") {
            runDir = "../run/server"
            configName = (if (isFabric) "Fabric" else if (!isNeo) "Forge" else "NeoForge") + "Server"
            ideConfigGenerated(true)
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
    minecraft(minecraft.versioned(mcVersion))

    mappings(loom.officialMojangMappings())

    if (isFabric) {
        modImplementation("net.fabricmc:fabric-loader:0.16.10")

        // Mainly for testing
        if (mcVersion > 11605)
            // TODO: addingVersion
            modLocalRuntime(fapi.versioned(mcVersion))
    } else {
        if (!isNeo) {
            "forge"(lexForge.versioned(mcVersion))
        } else {
            // TODO: addingVersion
            // snapshot version format:
            // "20.5.0-alpha.${mc[mcVersion]}.+"
            "neoForge"(neoForge.versioned(mcVersion))
        }
    }

    // Don't wanna deal with these atm
    if (mcVersion > 11605) {
        // These act like a dummy, technically only here to provide their modules/packages
        if (isFabric) {
            modCompileOnly("io.github.fabricators_of_create:Porting-Lib:${project.properties["port_lib_version_1_18_2"]}")
            modCompileOnly("com.simibubi.create:create-fabric-${project.properties["minecraft_version_1_18_2"]}:${project.properties["create_version_1_18_2"]}")
        } else {
            modCompileOnly("com.simibubi.create:create-1.18.2:0.5.1.e-318:slim") { isTransitive = false }
        }

        // <- EMI
        if (mcVersion <= 11802) {
            modCompileOnly(emi(api = true).versioned(mcVersion))
            if (project.properties["recipe_viewer"] == "emi" && isFabric)
                modLocalRuntime(emi().versioned(mcVersion))
        } else {
            modCompileOnly(emi(loaderName, api = true).versioned(mcVersion))
            if (project.properties["recipe_viewer"] == "emi")
                modLocalRuntime(emi(loaderName).versioned(mcVersion))
        }
        // EMI ->

        // <- REI
        // Use the full package instead of 'api-' for (neo)forge, since the 'api-' didn't include @REIPlugin*
        modCompileOnly(rei(loaderName, true).versioned(mcVersion))
        if (mcVersion >= 12002) {  // FIXME: Not sure why it's not included
            modCompileOnly("me.shedaniel.cloth:basic-math:0.6.1")
            modCompileOnly("dev.architectury:architectury:11.1.13")
        }
        if (project.properties["recipe_viewer"] == "rei") {
            if (mcVersion == 11902)  // REI's stupid dep bug
                modLocalRuntime("dev.architectury:architectury-fabric:6.5.77")
            modLocalRuntime(rei(loaderName).versioned(mcVersion))
        }
        // REI ->

        // <- JEI
        modCompileOnly(jei(mcVersion, isFabric, common = true, api = true).versioned(0))
        modCompileOnly(jei(mcVersion, isFabric, common = false, api = true).versioned(0))
        if (project.properties["recipe_viewer"] == "jei")
            modCompileOnly(jei(mcVersion, isFabric, common = false, api = false).versioned(0))
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
