package dependencies

val neoForge = Dependency(
    group = "net.neoforged",
    name = "neoforge",
    version = { mcVersion ->
        when (mcVersion) {
            in 12002..12003 -> "20.2.86"
            12004 -> "20.4.237"
            in 12005..12006 -> "20.6.121"
            in 12100..12101 -> "21.1.72"
            in 12102..12104 -> "21.3.1-beta"
            12105 -> "21.5.22-beta"
            else -> throw IllegalStateException("$mcVersion is not yet supported!")
        }
    },
)
