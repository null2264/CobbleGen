package dependencies

val minecraft = Dependency(
    group = "com.mojang",
    name = "minecraft",
    version = { mcVersion ->
        when (mcVersion.code) {
            // For snapshots
            //12100 -> "some snapshot"
            260100 -> "26.1-snapshot-2"
            else -> mcVersion.string
        }
    },
)
