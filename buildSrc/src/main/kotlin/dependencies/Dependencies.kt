package dependencies

data class Dependency(
    private val group: String,
    private val name: String,
    private val version: (Int, Int) -> String,
) {
    fun versioned(mcVersion: Int, mcBuild: Int): String = "${group}:${name}:${version(mcVersion, mcBuild)}"
}

fun versionStr(versionCode: Int, hotfix: Int = 0, alwaysShowHotfix: Boolean = false): String {
    val versionCodeStr = versionCode.toString()
    val major = versionCodeStr.getOrNull(0)?.toString() ?: "0"
    val minor = versionCodeStr
        .substring(1, 3 + (versionCodeStr.length - 5))  // Future proofing, in case "1.100.0" happened
        .padStart(2, '0')
        .trimStart('0')
    val patch = versionCodeStr
        .substring(3 + (versionCodeStr.length - 5))
        .padEnd(1, '0').trimStart('0')

    if (major.toInt() <= 1) {
        if (patch.isEmpty()) return "$major.$minor"
        return "$major.$minor.$patch"
    }

    return if (hotfix > 0 || alwaysShowHotfix) "$minor.$patch.$hotfix" else "$minor.$patch"
}
