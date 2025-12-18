package dependencies

val neoForge = Dependency(
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
            12111 -> "1-beta"
            else -> throw IllegalStateException("$mcVersion is not yet supported!")
        }
        val mc = versionStr(mcVersion).substring(2)

        "${mc}.${version}"
    },
)
