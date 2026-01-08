import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.architectury.plugin.ArchitectPluginExtension
import net.fabricmc.loom.LoomGradleExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import dependencies.minecraft as MC
import org.apache.tools.ant.filters.StripJavaComments

plugins {
    id("java")
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("io.github.null2264.architectury-loom") version "1.13-SNAPSHOT" apply false
    id("com.gradleup.shadow") apply false
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
}

val modVersion = System.getenv("VERSION") ?: project.properties["mod_version"] as? String ?: "0.0.0"

val loaderName = project.properties["null2264.platform"] as? String ?: ""
val isForge = loaderName.endsWith("forge")
val isNeo = loaderName.endsWith("neoforge")
val isFabric = loaderName.endsWith("fabric")
val mcVersionStr = project.properties["mcVer"] as? String ?: ""
val (major, minor, patch) = mcVersionStr
    .split(".")
    .toMutableList()
    .apply { while (this.size < 3) this.add("") }
val mcVersion: Int = "${major}${minor.padStart(2, '0')}${patch.padStart(2, '0')}".toInt()
val versionRange = supportedVersionRange(mcVersion, loaderName)

fun setupPreprocessor() {
    val buildProps = buildString {
        append("# DON'T TOUCH THIS FILE, This is handled by the build script\n")
        append("MC=${mcVersion}\n")
        if (isFabric) append("FABRIC=1\n")
        if (isForge) append("FORGE=${if (!isNeo) "1" else "2"}\n")
    }

    project.file("build.properties").writeText(buildProps)
}
setupPreprocessor()

architectury {
    minecraft = MC.versioned(mcVersion)
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    ext["mcVersion"] = mcVersion
    ext["mcVersionStr"] = mcVersionStr
    ext["loaderName"] = loaderName
    ext["isFabric"] = isFabric
    ext["isForge"] = isForge
    ext["isNeo"] = isNeo

    base.archivesName.set(rootProject.properties["archives_base_name"] as? String ?: "")

    val buildNumber: String? = System.getenv("GITHUB_RUN_NUMBER")
    version = buildString {
        append(modVersion)
        append("+")
        append(mcVersionStr)
        if (buildNumber != null) {
            append("b")
            append(buildNumber)
        }
        append("-")
        append(rootProject.properties["version_stage"])
        if (isFabric)
            append("-fabric")
        else
            append(if (isNeo) "-neoforge" else "-forge")
    }
    group = rootProject.properties["maven_group"] as String

    repositories {
        maven("https://jitpack.io")
        maven("https://maven.fabricmc.net/")
        maven {
            url = uri("https://maven.blamejared.com/")
            content {
                includeGroup("mezz.jei")
            }
        }
        maven("https://maven.gegy.dev/releases/")
        maven("https://maven.shedaniel.me/")
        maven("https://maven.terraformersmc.com/")
        maven("https://api.modrinth.com/maven/")
        maven("https://cursemaven.com/")
        maven("https://maven.jamieswhiteshirt.com/libs-release")
        maven("https://maven.theillusivec4.top/")
        maven("https://maven.neoforged.net/releases")
        maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
        maven("https://maven.createmod.net")
        mavenLocal()
        maven("https://mvn.devos.one/snapshots/")
        maven {
            url = uri("https://maven.tterrag.com/")
            content {
                // Tell tterrag's maven to stfu, it spits out timeout instead of 404 for some reason
                includeGroupByRegex("(com\\.)?tterrag")
            }
        }
    }

    tasks.withType<JavaCompile> {
        // ensure that the encoding is set to UTF-8, no matter what the system default is
        // this fixes some edge cases with special characters not displaying correctly
        // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
        // If Javadoc is generated, this must be specified in that task too.
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xplugin:Manifold --no-bootstrap")
    }
}

subprojects {
    // NOTE: This here for when I finally split the API to its own module, hopefully on v6.0
    val isApi = false;  // APIs shouldn't contain anything Minecraft related
    val isModModule = project == project(":cobblegen")
    // Modules that contains MC-related stuff
    val isMcModule = isModModule || project == project(":mclib")

    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")

    if (isModModule) {
        // NOTE: This must be set before archloom is applied!
        extra.set("loom.platform", loaderName)
    }

    if (isMcModule) {
        apply(plugin = "architectury-plugin")
        apply(plugin = "io.github.null2264.architectury-loom")
        val arch = project.extensions["architectury"] as ArchitectPluginExtension
        arch.apply {
            if (isModModule)
                loader(loaderName)
            else
                common(listOf(loaderName))
        }

        if (mcVersion < 260100) {
            val loom = project.extensions["loom"] as LoomGradleExtension
            loom.apply {
                silentMojangMappingsLicense()
            }
        }
    }

    val manifoldVersion = project.properties["manifold_version"] as? String ?: ""

    val shade: Configuration by configurations.creating {
        configurations.implementation.get().extendsFrom(this)
    }
    val shadeInternal: Configuration by configurations.creating

    val loaderProd = when {
        isFabric -> "Fabric"
        isNeo -> "NeoForge"
        isForge -> "Forge"
        else -> throw IllegalStateException("Unknown loader!")
    }

    // For internal libraries
    val compileInternal: Configuration by configurations.creating {
        configurations.compileClasspath.get().extendsFrom(this)
        configurations.runtimeClasspath.get().extendsFrom(this)
        if (isModModule) {
            configurations["development$loaderProd"].extendsFrom(this)
        }
    }

    val manifoldCompile: Configuration by configurations.creating {
        configurations.compileOnly.get().extendsFrom(this)
        configurations.annotationProcessor.get().extendsFrom(this)
        configurations.testAnnotationProcessor.get().extendsFrom(this)
    }

    if (isModModule && !isFabric) {
        configurations.named("forgeRuntimeLibrary").get().extendsFrom(shade)
    }

    val loom by lazy {
        if (!isMcModule) {
            throw IllegalStateException("Loom only available for MC modules")
        }
        project.the<LoomGradleExtensionAPI>()
    }

    dependencies {
        if (isMcModule) {
            val minecraft by configurations
            val mappings by configurations

            minecraft(MC.versioned(mcVersion))
            if (mcVersion < 260100) {
                mappings(loom.officialMojangMappings())
            }
        }

        shade("blue.endless:jankson:${project.properties["jankson_version"]}")

        shade("systems.manifold:manifold-ext-rt:${manifoldVersion}")
        manifoldCompile("systems.manifold:manifold-ext:${manifoldVersion}")
        manifoldCompile("systems.manifold:manifold-preprocessor:${manifoldVersion}")

        if (isModModule) {
            compileOnly(project(":stubs"))
            compileInternal(project(":mclib", configuration = "namedElements")) { isTransitive = false }
            shadeInternal(project(":mclib", configuration = "transformProduction$loaderProd")) {
                // Remove Junit test libraries
                exclude(group = "org.junit.jupiter", module = "junit-jupiter")
                exclude(group = "org.junit.jupiter", module = "junit-jupiter-engine")
                exclude(group = "junit", module = "junit")
                // Removed dependencies
                isTransitive = false
            }

            if (mcVersion <= 11605) {
                // slf4j is not included by MC in 1.16.5
                shade("org.slf4j:slf4j-api:1.7.36")
                shade("org.apache.logging.log4j:log4j-slf4j-impl:2.8.1")
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
        relocate("io.github.null2264.cobblegen.extensions", "io.github.null2264.shadowed.cobblegen.extensions")
        if (isFabric) {
            exclude("META-INF/mods.toml")
            exclude("META-INF/neoforge.mods.toml")
        } else if (isForge) {
            exclude("fabric.mod.json")
            exclude(if (isNeo && mcVersion >= 12006) "META-INF/mods.toml" else "META-INF/neoforge.mods.toml")
        }
        exclude("architectury.common.json")

        configurations = listOf(shade, shadeInternal)
        archiveClassifier.set("dev-shade")
        mergeServiceFiles()
    }

    artifacts.add("archives", shadowJar)

    val processResources by tasks.getting(ProcessResources::class) {
        if (!isModModule) return@getting

        val metadataVersion = "${modVersion}-${project.properties["version_stage"]}"
        val metadataMCVersion = if (isForge) versionRange.mavenStyle() else versionRange.semverStyle()
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
            filter<StripJavaComments>()
            expand(properties)
        }

        filesMatching("cobblegen.mixins.json") {
            filter<StripJavaComments>()
        }

        doLast {
            // For some reason Mojang rename the structure directory on MC 1.21 to singular form
            val structureDirName = if (mcVersion >= 12100) "structure" else "structures"
            if (isFabric || mcVersion >= 12105) {
                project.file("build/resources/main/data/cobblegen/gametest/${structureDirName}/").mkdirs()
                project.file("build/resources/main/data/cobblegen/gametest/${structureDirName}/empty.snbt")
                    .writeStructureAsSnbt(generateStructure(false))
            } else {
                project.file("build/resources/main/data/cobblegen/${structureDirName}/").mkdirs()
                project.file("build/resources/main/data/cobblegen/${structureDirName}/empty.nbt")
                    .writeStructureAsNbt(generateStructure(true))
            }

            project.file("build/resources/main/META-INF/mods.toml")
                .processModsToml(mcVersion, if (!isForge) 0 else (if (isNeo) 2 else 1))

            // We can't preprocess resources files with Manifold, so we'll construct the json files manually here instead.
            project.file("build/resources/main/cobblegen.mixins.json").processMixinsJson(mcVersion, isFabric)
            if (isFabric) project.file("build/resources/main/fabric.mod.json").processFabricModJson(mcVersion)
        }
    }

    val targetJavaVersion = if (!isApi) {
        when (mcVersion) {
            in 11200..11605 -> 8
            in 11700..11701 -> 16
            in 11800..12004 -> 17
            in 12005..12111 -> 21
            else -> 25
        }
    } else {
        8  // APIs should always target Java 8
    }
    tasks.withType<JavaCompile> {
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

    // In case I decided to split :cobblegen to multi-loader modules
//    tasks.create<Copy>("copyCommonLoaderResources") {
//        from(project(":common").file("src/main/resources/....")) {
//            into(file(project.file("build/resources/main")))
//            rename("...", "...")
//        }
//    }
}

//tasks.create("testMcVersions") {
//    println(mcVersions(supportedVersionRange(11902, "fabric"))
//        .map { if (it.isPreRelease) it.copy(preRelease = "Snapshot") else it }.distinct().map { it.toMojangString() })
//}

publishMods {
    val mainProject = project(":cobblegen")
    file.set(mainProject.file("build/libs/${rootProject.properties["archives_base_name"]}-${mainProject.version}.jar"))
    val releaseVersions = mcVersions(versionRange)
    displayName.set(
        buildString {
            append("[")
            if (isFabric) {
                append("FABRIC")
            } else {
                if (isNeo) append("NEOFORGE") else append("FORGE")
            }
            append(" MC")
            append(releaseVersions[0])
            if (releaseVersions.size > 1) append("+")
            append("]")
            append(" v")
            append(modVersion)
            append("-")
            append(rootProject.properties["version_stage"])
            if (mcVersion <= 11605) append(" (LITE)")
        }
    )
    changelog.set(System.getenv("CHANGELOG") ?: "Please visit our [releases](https://github.com/null2264/CobbleGen/releases) for a changelog")
    version.set(mainProject.version.toString())
    if (isFabric) {
        modLoaders.add("fabric")
        modLoaders.add("quilt")
    } else {
        if (mcVersion <= 12002 && !isNeo)  // No more LexForge, LexForge is too buggy
            modLoaders.add("forge")
        if (mcVersion == 12001 || isNeo)
            modLoaders.add("neoforge")
    }
    type = when(rootProject.properties["version_stage"]) {
        "ALPHA" -> ALPHA
        "BETA" -> BETA
        else -> STABLE
    }

    val cfToken = System.getenv("CURSEFORGE")
    if (cfToken != null) {
        curseforge {
            accessToken = cfToken
            projectId.set(rootProject.properties["curseforge_project"] as String)
            // Because CF did it the lazy way and just group every snapshot as a single snapshot
            minecraftVersions =
                releaseVersions
                    .map { if (it.isPreRelease) it.copy(preRelease = "Snapshot") else it }
                    .distinct()
                    .map { it.toMojangString() }

            embeds {
                slug = "jankson"
            }
        }
    }

    val mrToken = System.getenv("MODRINTH")
    if (mrToken != null) {
        modrinth {
            accessToken = mrToken
            projectId.set(rootProject.properties["modrinth_project"] as String)

            minecraftVersions =
                releaseVersions.map { it.toMojangString() }
        }
    }
}
