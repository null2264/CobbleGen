import org.gradle.kotlin.dsl.provideDelegate

data class CGVer(
    val code: Int,
    var alwaysShowHotfix: Boolean = false,
) {
    internal fun legacyVersionStr(versionCode: Int): String {
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

    val isWildcard: Boolean
        get() = code < 0

    val string: String
        get() {
            assert(!isWildcard) { "Can't transform a wildcard to string format!" }

            val versionCodeStr = code.toString()
            if (versionCodeStr.length == 5) return legacyVersionStr(code)

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

    override fun toString(): String {
        return string
    }

    companion object {
        fun fromString(string: String, alwaysShowHotfix: Boolean = false): CGVer {
            val (major, minor, patch) = string
                .split(".")
                .toMutableList()
                .apply { while (this.size < 3) this.add("") }
            return CGVer(
                "${major}${minor.padStart(2, '0')}${patch.padStart(2, '0')}".toInt(),
                alwaysShowHotfix,
            )
        }

        fun wildcard() = CGVer(-1)
    }
}
