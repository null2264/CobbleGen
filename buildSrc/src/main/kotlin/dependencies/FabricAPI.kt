package dependencies

val fapi = Dependency(
    group = "net.fabricmc.fabric-api",
    name = "fabric-api",
    version = { mcVersion ->
        when (mcVersion.code) {
            11605 -> "0.42.0+1.16"
            11802 -> "0.76.0+1.18.2"
            in 11900..11902 -> "0.76.0+1.19.2"
            in 11903..11904 -> "0.83.0+1.19.4"
            in 12000..12001 -> "0.83.1+1.20.1"
            in 12002..12004 -> "0.89.0+1.20.2"
            in 12005..12006 -> "0.100.8+1.20.6"
            in 12100..12101 -> "0.106.0+1.21.1"
            in 12102..12104 -> "0.106.1+1.21.3"
            in 12105..12110 -> "0.119.9+1.21.5"
            12111 -> "0.139.5+1.21.11"
            260100 -> "0.141.1+26.1"
            else -> throw IllegalStateException("$mcVersion is not yet supported!")
        }
    },
)

val fapiGameTest = Dependency(
    group = "net.fabricmc.fabric-api",
    name = "fabric-gametest-api-v1",
    version = { mcVersion ->
        when (mcVersion.code) {
            in 12105..12110 -> "3.1.2+2a6ec84b49"
            12111 -> "3.1.27+4fc5413f3e"
            260100 -> "4.0.0+574290bac9"
            else -> throw IllegalStateException("$mcVersion is not yet supported!")
        }
    },
)

val fapiResourceLoader = Dependency(
    group = "net.fabricmc.fabric-api",
    name = "fabric-resource-loader-v0",
    version = { mcVersion ->
        when (mcVersion.code) {
            12105 -> "3.1.6+02ca679649"
            else -> throw IllegalStateException("$mcVersion is not yet supported!")
        }
    },
)
