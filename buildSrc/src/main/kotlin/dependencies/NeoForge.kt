package dependencies

val NEO = Dependency(
    group = "net.neoforged",
    name = "neoforge",
    version = { mcVersion ->
        val version = when (mcVersion) {
            // snapshot version format:
            // "0-alpha.${mc[mcVersion]}.+"
            in 12002..12003 -> "86"
            12004 -> "237"
            in 12005..12006 -> "121"
            in 12100..12101 -> "129"
            in 12102..12104 -> "1-beta"
            in 12105..12110 -> "25-beta"
            12111 -> "24-beta"
            260100 -> "0-alpha.4+snapshot-1"
            else -> throw IllegalStateException("Version $mcVersion is not yet supported!")
        }
        val mc = versionStr(mcVersion, true).let { if (mcVersion>=260100) it else it.substring(2) }

        "${mc}.${version}"
    },
)

val NEOFORM = Dependency(
    group = "net.neoforged",
    name = "neoforge",
    version = { mcVersion ->
        val version = when (mcVersion) {
            in 12002..12003 -> "20241215.201144"
            12004 -> "20240627.114801"
            in 12005..12006 -> "20240627.102356"
            in 12100..12101 -> "20240808.144430"
            in 12102..12104 -> "20241023.131943"
            in 12105..12110 -> "20250325.162830"
            12111 -> "20251209.172050"
            260100 -> "snapshot-5-1"
            else -> throw IllegalStateException("Version $mcVersion is not yet supported!")
        }
        val mc = versionStr(mcVersion, false)

        "${mc}-${version}"
    }
)
