/**
 * @param upper No longer supported version
 * @param lower Last supported version
 */
data class VersionRange(
    private val upper: String?,
    private val lower: String?,
) {
    fun mavenStyle(): String {
        require(upper != null || lower != null) { "Upper and lower can't all be null" }

        return buildString {
            append("(")
            if (upper != null) append(upper.replace(".x", ".9999", true))
            append(",")
            if (lower != null) append(lower.replace(".x", ".9999", true))
            append("]")
        }
    }

    fun semverStyle(): String {
        require(upper != null || lower != null) { "Upper and lower can't all be null" }

        return buildString {
            if (upper != null) {
                append(">")
                append(upper.replace(".x", ".9999", true))
            }
            if (lower != null) {
                append(" <=")
                append(lower.replace(".x", ".9999", true))
            }
        }
    }
}

// FIXME: Exact version for 1.16.5 and 1.18.2
fun supportedVersionRange(mcVersion: Int, loader: String): VersionRange {
    return when (mcVersion) {
        11605 -> VersionRange("1.16.4", "1.16.5")
        11802 -> VersionRange("1.18.1", "1.18.2")
        in 11900..11902 -> VersionRange("1.18.x", "1.19.2")
        in 11903..11904 -> VersionRange("1.19.2", "1.19.4")
        in 12000..12001 -> VersionRange("1.19.x", "1.20.1")
        in 12002..12003 -> VersionRange("1.20.1", if (loader != "neoforge") "1.20.4" else "1.20.3")
        12004 -> VersionRange("1.20.3", "1.20.4")  // for Neo
        in 12005..12006 -> VersionRange("1.20.4", "1.20.6")
        in 12100..12101 -> VersionRange("1.20.x", "1.21.1")
        in 12102..12104 -> VersionRange("1.21.1", "1.21.4")
        12105 -> VersionRange("1.21.4", null)  // 1.21.5 or newer
        else -> VersionRange(null, null)
    }
}
