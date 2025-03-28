plugins {
    id("java")
    id("dev.architectury.loom") version "1.9-SNAPSHOT" apply false
    id("com.gradleup.shadow")
    id("systems.manifold.manifold-gradle-plugin") version "0.0.2-alpha"
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

fun setupPreprocessor() {
    val buildProps = buildString {
        append("# DON'T TOUCH THIS FILE, This is handled by the build script\n")
        append("MC=${mcVersion}\n")
        if (isFabric) append("FABRIC=1\n")
        if (isForge) append("FORGE=${if (!isNeo) "1" else "2"}\n")
    }

    File(projectDir, "build.properties").writeText(buildProps)
}
setupPreprocessor()

allprojects {
    apply(plugin = "java")

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
}

subprojects {
    apply(plugin = "systems.manifold.manifold-gradle-plugin")
    extra.set("loom.platform", rootProject.properties["loaderName"] as? String ?: "fabric")
    manifold {
        manifoldVersion.set(rootProject.properties["manifold_version"] as String)
    }
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
