package dependencies

val NEO = Dependency(
    group = "net.neoforged",
    name = "neoforge",
    version = { mcVersion ->
        val version = when (mcVersion.code) {
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
        val mc = mcVersion
            .let { it.alwaysShowHotfix = true; it }
            .string
            .let { if (mcVersion.code >= 260100) it else it.substring(2) }

        "${mc}.${version}"
    },
)
