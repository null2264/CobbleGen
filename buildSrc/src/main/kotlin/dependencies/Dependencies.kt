package dependencies

data class Dependency(
    private val group: String,
    private val name: String,
    private val version: (Int) -> String,
) {
    fun versioned(mcVersion: Int): String = "${group}:${name}:${version(mcVersion)}"
}

fun versionStr(versionCode: Int): String {
    val versionCodeStr = versionCode.toString()
    val major = versionCodeStr.getOrNull(0)?.toString() ?: "0"
    val minor = versionCodeStr
        .substring(1, 3 + (versionCodeStr.length - 5))  // Future proofing, in case "1.100.0" happened
        .padStart(2, '0')
        .trimStart('0')
    val patch = versionCodeStr
        .substring(3 + (versionCodeStr.length - 5))
        .padEnd(1, '0').trimStart('0')

    if (patch.isEmpty()) return "$major.$minor"
    return "$major.$minor.$patch"
}
