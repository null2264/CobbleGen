package dependencies

val lexForge = Dependency(
    group = "net.minecraftforge",
    name = "forge",
    version = { mcVersion ->
        val version = when (mcVersion) {
            11605 -> "36.2.41"
            11802 -> "40.2.9"
            in 11900..11902 -> "43.2.14"
            in 11903..11904 -> "45.1.0"
            in 12000..12001 -> "47.0.3"
            in 12002..12004 -> "48.0.13"
            else -> throw IllegalStateException("Forge no longer supported!")
        }
        "${versionStr(mcVersion)}-${version}"
    },
)
