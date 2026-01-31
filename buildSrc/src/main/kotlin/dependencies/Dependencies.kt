package dependencies

import CGVer

data class Dependency(
    private val group: String,
    private val name: String,
    val version: (CGVer) -> String,
) {
    fun versioned(mcVersion: CGVer): String = "${group}:${name}:${version(mcVersion)}"
}

fun legacyVersionStr(versionCode: Int): String {
    val versionCodeStr = versionCode.toString()
    val major = versionCodeStr.getOrNull(0)?.toString() ?: "0"
    val minor = versionCodeStr
        .substring(1, 3)
        .padStart(2, '0')
        .trimStart('0')
    val patch = versionCodeStr
        .substring(3)
        .padEnd(1, '0').trimStart('0')
        
    if (patch.isEmpty()) return "$major.$minor"
    return "$major.$minor.$patch"
}

/**
 * Transform version code (e.g. 260100) back to formatted string (e.g. 26.01).
 */
fun versionStr(versionCode: Int, alwaysShowHotfix: Boolean = false): String {
    val versionCodeStr = versionCode.toString()
    if (versionCodeStr.length == 5) return legacyVersionStr(versionCode)

    val year = versionCodeStr.substring(0, 2)
    val release = versionCodeStr
        .substring(2, 4)
        .trimStart('0')
        .let { if (it == "") "0" else it }
    val hotfix = versionCodeStr
        .substring(4)
        .trimStart('0')
        .let { if (it == "" && alwaysShowHotfix) "0" else it }

    return if (hotfix != "") "$year.$release.$hotfix" else "$year.$release"
}
