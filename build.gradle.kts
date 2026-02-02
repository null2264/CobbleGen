import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.StripJavaComments

plugins {
    id("java")
    // Explicitly add idea-ext here since without it being here :stubs and :fabric would both try to apply it at the same time causing error
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.3" apply false
    id("com.gradleup.shadow") apply false
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
}

val modVersion = System.getenv("VERSION") ?: project.properties["mod_version"] as? String ?: "0.0.0"

val loaderName = project.properties["null2264.platform"] as? String ?: ""
val mcVersionStr = project.properties["mcVer"] as? String ?: ""
val mcVersion = CGVer.fromString(mcVersionStr)
val versionRange = supportedVersionRange(mcVersion, loaderName)

fun setupPreprocessor() {
    val buildProps = buildString {
        append("# DON'T TOUCH THIS FILE, This is handled by the build script\n")
        append("MC=${mcVersion.code}\n")
    }

    project.file("build.properties").writeText(buildProps)
}
setupPreprocessor()

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    extra["mcVersion"] = mcVersion
    extra["mcVersionStr"] = mcVersionStr
    extra["loaderName"] = loaderName
    val isFabric = project == project(":fabric")
    val isNeo = !isFabric && mcVersion.code >= 12002

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
        maven("https://libraries.minecraft.net/")
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
    val isModModule = project in listOf(project(":fabric"), project(":forge"))
    val isFabric = project == project(":fabric")
    val isForge = project == project(":forge")
    val isNeo = isForge && mcVersion.code >= 12002

    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")

    val manifoldVersion = project.properties["manifold_version"] as? String ?: ""

    val shade: Configuration by configurations.creating {
        configurations.implementation.get().extendsFrom(this)
    }
    val shadeInternal: Configuration by configurations.creating

    val manifoldCompile: Configuration by configurations.creating {
        configurations.compileOnly.get().extendsFrom(this)
        configurations.annotationProcessor.get().extendsFrom(this)
        configurations.testAnnotationProcessor.get().extendsFrom(this)
    }

    if (isForge) {
        afterEvaluate {
            if (mcVersion.code <= 12108) {
                configurations.named("additionalRuntimeClasspath").get().extendsFrom(shade)
            }
        }
    }

    dependencies {
        shade("blue.endless:jankson:${project.properties["jankson_version"]}")

        shade("systems.manifold:manifold-ext-rt:${manifoldVersion}")
        manifoldCompile("systems.manifold:manifold-ext:${manifoldVersion}")
        manifoldCompile("systems.manifold:manifold-preprocessor:${manifoldVersion}")

        if (isModModule) {
            if (mcVersion.code <= 11605) {
                // slf4j is not included by MC in 1.16.5
                shade("org.slf4j:slf4j-api:1.7.36")
                shade("org.apache.logging.log4j:log4j-slf4j-impl:2.8.1")
            }
        }
    }

    val shadowJar by tasks.getting(ShadowJar::class) {
        isZip64 = true
        relocate("blue.endless.jankson", "io.github.null2264.shadowed.jankson")
        if (mcVersion.code <= 11605) {
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
            exclude(if (isNeo && mcVersion.code >= 12006) "META-INF/mods.toml" else "META-INF/neoforge.mods.toml")
        }
        exclude("architectury.common.json")

        configurations = listOf(shade, shadeInternal)
        archiveClassifier.set("dev-shade")
        mergeServiceFiles()
    }

    artifacts.add("archives", shadowJar)

    tasks.withType<ProcessResources> {
        if (!isModModule) return@withType

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
                if (isNeo && mcVersion.code >= 12006) "META-INF/neoforge.mods.toml" else "META-INF/mods.toml"
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
            val structureDirName = if (mcVersion.code >= 12100) "structure" else "structures"
            if (isFabric || mcVersion.code >= 12105) {
                project.file("build/resources/main/data/cobblegen/gametest/${structureDirName}/").mkdirs()
                project.file("build/resources/main/data/cobblegen/gametest/${structureDirName}/empty.snbt")
                    .writeStructureAsSnbt(generateStructure(false))
            } else {
                project.file("build/resources/main/data/cobblegen/${structureDirName}/").mkdirs()
                project.file("build/resources/main/data/cobblegen/${structureDirName}/empty.nbt")
                    .writeStructureAsNbt(generateStructure(true))
            }


            // We can't preprocess resources files with Manifold, so we'll construct the json files manually here instead.
            project.file("build/resources/main/cobblegen.mixins.json").processMixinsJson(mcVersion)
            project.file("build/resources/main/cobblegen.${project.name}.mixins.json").apply {
                if (isFabric) processMixinsJsonFabric(mcVersion)
                else processMixinsJsonForge(mcVersion)
            }
            if (isFabric) project.file("build/resources/main/fabric.mod.json").processFabricModJson(mcVersion)
            else project.file("build/resources/main/$metadataFilename").processModsToml(mcVersion, if (mcVersion.code >= 12002) 2 else 1)
        }
    }

    val targetJavaVersion = if (!isApi) {
        when (mcVersion.code) {
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

//publishMods {
//    val mainProject = project(":cobblegen")
//    file.set(mainProject.file("build/libs/${rootProject.properties["archives_base_name"]}-${mainProject.version}.jar"))
//    val releaseVersions = mcVersions(versionRange)
//    displayName.set(
//        buildString {
//            append("[")
//            if (isFabric) {
//                append("FABRIC")
//            } else {
//                if (isNeo) append("NEOFORGE") else append("FORGE")
//            }
//            append(" MC")
//            append(releaseVersions[0])
//            if (releaseVersions.size > 1) append("+")
//            append("]")
//            append(" v")
//            append(modVersion)
//            append("-")
//            append(rootProject.properties["version_stage"])
//            if (mcVersion.code <= 11605) append(" (LITE)")
//        }
//    )
//    changelog.set(System.getenv("CHANGELOG") ?: "Please visit our [releases](https://github.com/null2264/CobbleGen/releases) for a changelog")
//    version.set(mainProject.version.toString())
//    if (isFabric) {
//        modLoaders.add("fabric")
//        modLoaders.add("quilt")
//    } else {
//        if (mcVersion.code <= 12002 && !isNeo)  // No more LexForge, LexForge is too buggy
//            modLoaders.add("forge")
//        if (mcVersion.code == 12001 || isNeo)
//            modLoaders.add("neoforge")
//    }
//    type = when(rootProject.properties["version_stage"]) {
//        "ALPHA" -> ALPHA
//        "BETA" -> BETA
//        else -> STABLE
//    }
//
//    val cfToken = System.getenv("CURSEFORGE")
//    if (cfToken != null) {
//        curseforge {
//            accessToken = cfToken
//            projectId.set(rootProject.properties["curseforge_project"] as String)
//            // Because CF did it the lazy way and just group every snapshot as a single snapshot
//            minecraftVersions =
//                releaseVersions
//                    .map { if (it.isPreRelease) it.copy(preRelease = "Snapshot") else it }
//                    .distinct()
//                    .map { it.toMojangString() }
//
//            embeds {
//                slug = "jankson"
//            }
//        }
//    }
//
//    val mrToken = System.getenv("MODRINTH")
//    if (mrToken != null) {
//        modrinth {
//            accessToken = mrToken
//            projectId.set(rootProject.properties["modrinth_project"] as String)
//
//            minecraftVersions =
//                releaseVersions.map { it.toMojangString() }
//        }
//    }
//}
