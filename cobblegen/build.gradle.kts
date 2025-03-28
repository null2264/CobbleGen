import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("dev.architectury.loom") version "1.9-SNAPSHOT"
    id("com.gradleup.shadow")
}

val loaderName = project.properties["loaderName"] as? String ?: "fabric"
val isForge = loaderName.endsWith("forge")
val isNeo = loaderName.endsWith("neoforge")
val isFabric = loaderName.endsWith("fabric")
val mcVersionStr = project.properties["mcVer"] as? String ?: ""
val (major, minor, patch) = mcVersionStr
    .split(".")
    .toMutableList()
    .apply { if (this.size < 3) this.add("") }
val mcVersion: Int = "${major}${minor.padStart(2, '0')}${patch.padStart(2, '0')}".toInt()
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
            runDir = "../../run/client"
            ideConfigGenerated(true)
        }
        named("server") {
            runDir = "../../run/server"
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

val shade: Configuration by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

dependencies {
    // TODO(addingVersion): For snapshots
    val mc: Map<Int, String> = mapOf(
    )
    minecraft("com.mojang:minecraft:${mc[mcVersion] ?: mcVersionStr}")

    mappings(loom.officialMojangMappings())

    if (isFabric) {
        if (mcVersion <= 11902 && project.properties["recipe_viewer"] == "rei")
            modImplementation("net.fabricmc:fabric-loader:0.14.14")  // I don't get it, REI hate 0.14.21 in 1.19.2 or lower, wtf?
        else if (mcVersion <= 12001)
            modImplementation("net.fabricmc:fabric-loader:0.14.21")
        else
            modImplementation("net.fabricmc:fabric-loader:0.16.7")

        // For testing
        if (project.properties["recipe_viewer"] != "none" && mcVersion > 11605)
            // TODO: addingVersion
            modLocalRuntime("net.fabricmc.fabric-api:fabric-api:" + mapOf(
                11605 to "0.42.0+1.16",
                11802 to "0.76.0+1.18.2",
                11902 to "0.76.0+1.19.2",
                11904 to "0.83.0+1.19.4",
                12001 to "0.83.1+1.20.1",
                12002 to "0.89.0+1.20.2",
                12006 to "0.100.8+1.20.6",
                12101 to "0.106.0+1.21.1",
                12103 to "0.106.1+1.21.3",
            )[mcVersion])
    } else {
        if (!isNeo) {
            "forge"("net.minecraftforge:forge:${mcVersionStr}-" + mapOf(
                11605 to "36.2.41",
                11802 to "40.2.9",
                11902 to "43.2.14",
                11904 to "45.1.0",
                12001 to "47.0.3",
                12002 to "48.0.13",
                // LexForge is no longer supported
            )[mcVersion])
        } else {
            // TODO: addingVersion
            // snapshot version format:
            // "20.5.0-alpha.${mc[mcVersion]}.+"
            "neoForge"("net.neoforged:neoforge:" + mapOf(
                12002 to "20.2.86",
                12004 to "20.4.237",
                12006 to "20.6.121",
                12101 to "21.1.72",
                12103 to "21.3.1-beta",
            )[mcVersion])
        }
    }

    shade("blue.endless:jankson:${project.properties["jankson_version"]}")
    if (!isFabric)
        "forgeRuntimeLibrary"("blue.endless:jankson:${project.properties["jankson_version"]}")

    val manifoldVersion = project.properties["manifold_version"] as? String ?: ""
    shade("systems.manifold:manifold-ext-rt:${manifoldVersion}")
    if (!isFabric)
        "forgeRuntimeLibrary"("systems.manifold:manifold-ext-rt:${manifoldVersion}")
    annotationProcessor("systems.manifold:manifold-ext:${manifoldVersion}")
    testAnnotationProcessor("systems.manifold:manifold-ext:${manifoldVersion}")
    annotationProcessor("systems.manifold:manifold-preprocessor:${manifoldVersion}")
    testAnnotationProcessor("systems.manifold:manifold-preprocessor:${manifoldVersion}")

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
        if (mcVersion <= 11802 && isFabric) {
            modCompileOnly("dev.emi:emi:0.7.3+${mcVersionStr}:api")
            if (project.properties["recipe_viewer"] == "emi")
                modLocalRuntime("dev.emi:emi:0.7.3+${mcVersionStr}")
        } else {
            // TODO: addingVersion - EMI. They didn't break API on MC version upgrade so mismatch should be fine
            val suffix = mapOf(
                11902 to "1.19.2",
                11904 to "1.19.4",
                12001 to "1.20.1",
                12002 to "1.20.2",
                12004 to "1.20.2",  // For Neo, the same 1.20.2
                12006 to "1.20.6",
                12101 to "1.21.1",
                12103 to "1.21.1", // FIXME: .
            )
            val emiVersion = "1.1.18+${suffix[mcVersion] ?: "1.20.2"}"
            // EMI support multiple platform since 1.0.0
            // EMI seems to also skip 1.19 and 1.19.1
            modCompileOnly("dev.emi:emi-${if (isFabric) "fabric" else (if (mcVersion >= 12006) "neoforge" else "forge")}:$emiVersion:api")
            if (project.properties["recipe_viewer"] == "emi" && suffix[mcVersion] != null)
                modLocalRuntime("dev.emi:emi-${if (isFabric) "fabric" else (if (mcVersion >= 12006) "neoforge" else "forge")}:$emiVersion")
        }
        // EMI ->

        // <- REI
        // TODO: addingVersion - REI
        val reiVersions = mapOf(
            11802 to "8.3.618",
            11902 to "9.1.619",
            11904 to "11.0.621",
            12001 to "12.0.625",
            12002 to "13.0.685",
            12004 to "13.0.685",  // for Neo
            12006 to "15.0.787",
            12101 to "16.0.788",
            12103 to "17.0.789",
        )
        val reiFallback = "17.0.789"
        // Use the full package instead of 'api-' for (neo)forge, since the 'api-' didn't include @REIPlugin*
        modCompileOnly("me.shedaniel:RoughlyEnoughItems-${if (isFabric) "api-fabric" else if (!isNeo) "forge" else "neoforge"}:${reiVersions[mcVersion] ?: reiFallback}")
        if (mcVersion >= 12002) {  // FIXME: Not sure why it's not included
            modCompileOnly("me.shedaniel.cloth:basic-math:0.6.1")
            modCompileOnly("dev.architectury:architectury:11.1.13")
        }
        if (project.properties["recipe_viewer"] == "rei" && reiVersions[mcVersion] != null) {
            if (mcVersion == 11902)  // REI's stupid dep bug
                modLocalRuntime("dev.architectury:architectury-fabric:6.5.77")
            modLocalRuntime("me.shedaniel:RoughlyEnoughItems-${if (isFabric) "fabric" else "forge"}:${reiVersions[mcVersion]}")
        }
        // REI ->

        // <- JEI
        // TODO: addingVersion - JEI
        val jeiVersions = mapOf(
            11802 to "10.2.1.1004",
            11902 to "11.6.0.1015",
            11904 to "13.1.0.13",
            12001 to "15.0.0.12",
            12002 to "16.0.0.28",
            12004 to "16.0.0.28",  // for Neo
            12006 to "18.0.0.62",
            12101 to "19.21.0.246",
            12103 to null,
        )
        val jeiVersion = jeiVersions[mcVersion]
        // <- fallback - should be the latest version
        val fallbackJeiVer = "19.21.0.246"
        val fallbackJeiMcVer = "1.21.1"
        // fallback ->
        val jeiMc = mapOf(
            12004 to "1.20.2",  // for Neo
            12103 to fallbackJeiMcVer,
        )
        modCompileOnly("mezz.jei:jei-${jeiMc[mcVersion] ?: mcVersionStr}-common-api:${jeiVersion ?: fallbackJeiVer}")
        modCompileOnly("mezz.jei:jei-${jeiMc[mcVersion] ?: mcVersionStr}-${if (isFabric) "fabric" else "forge"}-api:${jeiVersion ?: fallbackJeiVer}")
        if (project.properties["recipe_viewer"] == "jei" && jeiVersion != null)
            modLocalRuntime("mezz.jei:jei-${jeiMc[mcVersion] ?: mcVersionStr}-${if (isFabric) "fabric" else "forge"}:${jeiVersion}")
        // JEI ->

        /* FIXME: Broken, somehow
        if (mcVersion == 11802 && isFabric) {
            modLocalRuntime("com.tterrag.registrate_fabric:Registrate:MC1.18.2-1.1.7")
            modLocalRuntime("io.github.fabricators_of_create:Porting-Lib:${project.port_lib_version_1_18_2}")
            modLocalRuntime("com.simibubi.create:create-fabric-${project.minecraft_version_1_18_2}:${project.create_version_1_18_2}")
        }
         */
    } else {
        // slf4j is not included by MC in 1.16.5
        shade("org.slf4j:slf4j-api:1.7.36")
        shade("org.apache.logging.log4j:log4j-slf4j-impl:2.8.1")
        if (!isFabric) {
            "forgeRuntimeLibrary"("org.slf4j:slf4j-api:1.7.36")
            "forgeRuntimeLibrary"("org.apache.logging.log4j:log4j-slf4j-impl:2.8.1")
        }
    }
}

val shadowJar by tasks.getting(ShadowJar::class) {
    isZip64 = true
    relocate("blue.endless.jankson", "io.github.null2264.shadowed.jankson")
    if (mcVersion <= 11605) {
        relocate("org.slf4j", "io.github.null2264.shadowed.slf4j")
        relocate("org.apache.logging", "io.github.null2264.shadowed.log4j")
    }
    relocate("manifold", "io.github.null2264.shadowed.manifold")
    if (isFabric) {
        exclude("META-INF/mods.toml")
        exclude("META-INF/neoforge.mods.toml")
    } else if (isForge) {
        exclude("fabric.mod.json")
        exclude(if (isNeo && mcVersion >= 12006) "META-INF/mods.toml" else "META-INF/neoforge.mods.toml")
    }
    exclude("architectury.common.json")

    configurations = listOf(shade)
    archiveClassifier.set("dev-shade")
}

artifacts.add("archives", shadowJar)

val remapJar by tasks.getting(RemapJarTask::class) {
    dependsOn(shadowJar)
    inputFile.set(shadowJar.archiveFile)
}

// FIXME: Can no longer preprocess resources
val processResources by tasks.getting(ProcessResources::class) {
    val metadataVersion = "${project.properties["mod_version"]}-${project.properties["version_stage"]}"
    val metadataMCVersion =
            if (supportedVersionRange[0] != null) (
            (if (isFabric) ">=" else "[") +
            supportedVersionRange[0] +
            (if (supportedVersionRange[1] == null)
                    (if (isFabric) "" else ",)")
                    else ((if (isFabric) " <=" else ",") + supportedVersionRange[1] + (if (isFabric) "" else "]")))
            ) else (if (isFabric) supportedVersionRange[1] else "[${supportedVersionRange[1]}]")
    val properties = mapOf(
        "version" to metadataVersion,
        "mcversion" to metadataMCVersion,
        "forge" to (if (isNeo) "neoforge" else "forge"),
    )
    inputs.properties(properties)
    filteringCharset = Charsets.UTF_8.name()

    val metadataFilename =
        if (isFabric) {
            "fabric.mod.json"
        } else {
            if (isNeo && mcVersion >= 12006) "META-INF/neoforge.mods.toml" else "META-INF/mods.toml"
        }

    filesMatching(metadataFilename) {
        filter { line -> if (line.trim().startsWith("//")) "" else line }  // strip comments
        expand(properties)
    }
}

val targetJavaVersion = if (mcVersion >= 12006) 21 else (if (mcVersion >= 11700) 17 else 8)
tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    if (targetJavaVersion > 8) {
        options.release = targetJavaVersion
    }
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() != javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}
