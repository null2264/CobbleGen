import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompression
import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.NbtVariant
import net.benwoodworth.knbt.StringifiedNbt
import net.benwoodworth.knbt.add
import net.benwoodworth.knbt.addNbtCompound
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.encodeToStream
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.putNbtCompound
import net.benwoodworth.knbt.putNbtList
import org.apache.tools.ant.filters.StripJavaComments

plugins {
    id("java")
    id("dev.architectury.loom") version "1.10-SNAPSHOT" apply false
    id("com.gradleup.shadow") apply false
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
        classpath("net.benwoodworth.knbt:knbt:0.11.8")
    }
}

val loaderName = project.properties["loom.platform"] as? String ?: ""
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

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    ext["mcVersion"] = mcVersion
    ext["mcVersionStr"] = mcVersionStr
    ext["isFabric"] = isFabric
    ext["isForge"] = isForge
    ext["isNeo"] = isNeo

    base.archivesName.set(rootProject.properties["archives_base_name"] as? String ?: "")

    val buildNumber: String? = System.getenv("GITHUB_RUN_NUMBER")
    version = buildString {
        append(rootProject.properties["mod_version"])
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
        maven {
            url = uri("https://maven.blamejared.com/")
            content {
                includeGroup("mezz.jei")
            }
        }
        maven("https://maven.shedaniel.me/")
        maven("https://maven.terraformersmc.com/")
        maven("https://api.modrinth.com/maven/")
        maven("https://cursemaven.com/")
        maven("https://mvn.devos.one/snapshots/")
        maven("https://maven.jamieswhiteshirt.com/libs-release")
        maven("https://maven.tterrag.com/")
        maven("https://maven.theillusivec4.top/")
        maven("https://maven.neoforged.net/releases")
        maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
        mavenLocal()
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
    val isModModule = project == project(":cobblegen")

    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")
    if (isModModule) {
        apply(plugin = "dev.architectury.loom")
    }

    val manifoldVersion = project.properties["manifold_version"] as? String ?: ""

    val shade: Configuration by configurations.creating {
        configurations.implementation.get().extendsFrom(this)
    }

    // For internal libraries
    val compileInternal: Configuration by configurations.creating {
        shade.extendsFrom(this)
        configurations.compileClasspath.get().extendsFrom(this)
        configurations.runtimeClasspath.get().extendsFrom(this)
    }

    val manifoldCompile: Configuration by configurations.creating {
        configurations.compileOnly.get().extendsFrom(this)
        configurations.annotationProcessor.get().extendsFrom(this)
        configurations.testAnnotationProcessor.get().extendsFrom(this)
    }

    if (isModModule && !isFabric) {
        configurations.named("forgeRuntimeLibrary").get().extendsFrom(shade)
    }

    dependencies {
        shade("blue.endless:jankson:${project.properties["jankson_version"]}")

        shade("systems.manifold:manifold-ext-rt:${manifoldVersion}")
        manifoldCompile("systems.manifold:manifold-ext:${manifoldVersion}")
        manifoldCompile("systems.manifold:manifold-preprocessor:${manifoldVersion}")

        if (isModModule) {
            compileInternal(project(":mclib")) {
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

    val processResources by tasks.getting(ProcessResources::class) {
        if (!isModModule) return@getting

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
            filter<StripJavaComments>()
            expand(properties)
        }

        filesMatching("cobblegen.mixins.json") {
            filter<StripJavaComments>()
        }

        doLast {
            val nbt = Nbt {
                variant = NbtVariant.Java
                compression = NbtCompression.Gzip
            }
            val snbt = StringifiedNbt {}
            val data = buildNbtCompound {
                put("DataVersion", 2730)
                putNbtList<NbtInt>("size") {
                    add(8)
                    add(8)
                    add(8)
                }
                putNbtList("data") {
                    for (i in 0..7) {
                        for (j in 0..7) {
                            for (k in 0..7) {
                                addNbtCompound {
                                    putNbtList("pos") {
                                        add(i)
                                        add(j)
                                        add(k)
                                    }
                                    if (isFabric)
                                        put("state", "minecraft:air")
                                    else
                                        put("state", 0)
                                }
                            }
                        }
                    }
                }
                putNbtList<NbtString>("entities") {}
                if (isFabric)
                    putNbtList("palette") {
                        add("minecraft:air")
                    }
                else
                    putNbtList("palette") {
                        addNbtCompound {
                            put("Name", "minecraft:air")
                        }
                    }
            }
            if (isFabric) {
                project.file("build/resources/main/data/cobblegen/gametest/structures/").mkdirs()
                project.file("build/resources/main/data/cobblegen/gametest/structures/empty.snbt").writeText(
                    snbt.encodeToString(NbtCompound.serializer(), data)
                )
                // For some reason Fabric rename the directory on MC 1.21
                project.file("build/resources/main/data/cobblegen/gametest/structure/").mkdirs()
                project.file("build/resources/main/data/cobblegen/gametest/structure/empty.snbt").writeText(
                    snbt.encodeToString(NbtCompound.serializer(), data)
                )
            } else {  // For Forge-alike
                project.file("build/resources/main/data/cobblegen/structures/").mkdirs()
                project.file("build/resources/main/data/cobblegen/structures/empty.nbt").outputStream().use { output ->
                    nbt.encodeToStream(buildNbtCompound { put("", data) }, output)
                }
            }

            // We can't preprocess resources files with Manifold, so we'll construct the json files manually here instead.
            val prettyJson = Json { prettyPrint = true }
            @OptIn(ExperimentalSerializationApi::class)
            val lenientJson = Json {
                allowComments = true
                allowTrailingComma = true
            }
            fun MutableList<JsonElement>.addJson(value: String) {
                add(JsonPrimitive(value))
            }

            val mixinsFile = project.file("build/resources/main/cobblegen.mixins.json")
            val both = buildList {
                if (mcVersion >= 12005) addJson("network.packet.CustomPacketPayloadMixin")
                addJson("CommandsMixin")
                addJson("MinecraftServerMixin")
                if (mcVersion > 11605) {
                    addJson("create.CreateFluidReactionsMixin")
                    addJson("create.CreateFluidReactionsMixinPatchE")
                    if (isFabric) addJson("create.CreateFluidReactionsMixinPatchF")
                }
                addJson("fluid.FluidEventMixin")
                addJson("fluid.LavaEventMixin")
            }
            val client = buildList {
                if (mcVersion < 12005) addJson("network.packet.ClientboundCustomPayloadPacketMixin")
                addJson("network.ClientCommonPacketListenerMixin")
                addJson("network.ConnectionMixin")
            }
            val server = buildList {
                addJson("network.PlayerManagerMixin")
                if (mcVersion < 12002) addJson("network.ServerboundCustomPayloadPacketAccessor")
                else addJson("network.ServerConfigurationPacketListenerMixin")
                if (mcVersion < 12005) addJson("network.packet.ServerboundCustomPayloadPacketMixin")
                addJson("network.ServerCommonPacketListenerMixin")
            }
            val mixinsJson = JsonObject(
                lenientJson.decodeFromString<JsonObject>(mixinsFile.readText(Charsets.UTF_8)).toMutableMap().apply {
                    set("compatibilityLevel", JsonPrimitive(if (mcVersion <= 11605) "JAVA_8" else "JAVA_17"))
                    set("mixins", JsonArray(both))
                    set("client", JsonArray(client))
                    set("server", JsonArray(server))
                }
            )
            mixinsFile.writeText(prettyJson.encodeToString(JsonObject.serializer(), mixinsJson))

            if (!isFabric) return@doLast

            val fabricMetadataFile = project.file("build/resources/main/fabric.mod.json")
            val fabricMetadataJson = JsonObject(
                lenientJson.decodeFromString<JsonObject>(fabricMetadataFile.readText(Charsets.UTF_8)).toMutableMap().apply {
                    (get("entrypoints") as? JsonObject)?.toMutableMap()?.apply {
                        if (mcVersion > 11605) {
                            set("jei_mod_plugin", JsonArray(listOf(JsonPrimitive("io.github.null2264.cobblegen.integration.viewer.jei.CGJEIPlugin"))))
                            set("rei_client", JsonArray(listOf(JsonPrimitive("io.github.null2264.cobblegen.integration.viewer.rei.CGREIPlugin"))))
                            set("emi", JsonArray(listOf(JsonPrimitive("io.github.null2264.cobblegen.integration.viewer.emi.CGEMIPlugin"))))
                        }
                        set(
                            "cobblegen_plugin",
                            JsonArray(buildList {
                                addJson("io.github.null2264.cobblegen.integration.BuiltInPlugin")
                                if (mcVersion > 11605) addJson("io.github.null2264.cobblegen.integration.CreatePlugin")
                            }),
                        )
                    }?.let {
                        set("entrypoints", JsonObject(it))
                    }
                }
            )
            fabricMetadataFile.writeText(prettyJson.encodeToString(JsonObject.serializer(), fabricMetadataJson))
        }
    }

    val targetJavaVersion = if (mcVersion >= 12006) 21 else (if (mcVersion >= 11700) 17 else 8)
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

// TODO: addingVersion
val mcReleaseVersions = mapOf<Int, List<String>>(
    11605 to listOf("1.16.5"),
    11802 to listOf("1.18.2"),
    11902 to listOf("1.19", "1.19.1", "1.19.2"),
    11904 to listOf("1.19.3", "1.19.4"),
    12001 to listOf("1.20", "1.20.1"),
    12002 to listOf("1.20.2", "1.20.3").let {
        val rt = it.toMutableList()
        if (!isNeo) rt.add("1.20.4")

        rt
    },
    12004 to listOf("1.20.4"),  // for Neo
    12006 to listOf("1.20.5", "1.20.6"),
    12101 to listOf("1.21", "1.21.1"),
    12103 to listOf("1.21.2", "1.21.3", "1.21.4")
)[mcVersion] ?: throw IllegalStateException("Should not be empty!")

// These overwrites mcReleaseVersions
val cfSnapshots = mapOf<Int, List<String>>(
//    12102 to listOf("1.21.2-Snapshot"),
)[mcVersion]

// These overwrites mcReleaseVersions
val mrSnapshots = mapOf<Int, List<String>>(
//    12102 to listOf("1.21.2-pre3"),
)[mcVersion]
