package dependencies

val neoForge = Dependency(
    group = "net.neoforged",
    name = "neoforge",
    version = { mcVersion, hotfix ->
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
            22601 -> when (hotfix) {
                0 -> "0-alpha.4+snapshot-1"
                else -> throw IllegalStateException("Hotfix $hotfix is not yet supported!")
            }
            else -> throw IllegalStateException("Version $mcVersion is not yet supported!")
        }
        val mc = versionStr(mcVersion, hotfix, true).substring(2)

        "${mc}.${version}"
    },
)
