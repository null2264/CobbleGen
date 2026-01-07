package dependencies

val minecraft = Dependency(
    group = "com.mojang",
    name = "minecraft",
    version = { mcVersion ->
        when (mcVersion) {
            // For snapshots
            //12100 -> "some snapshot"
            260100 -> "26.1-snapshot-1"
            else -> versionStr(mcVersion)
        }
    },
)
