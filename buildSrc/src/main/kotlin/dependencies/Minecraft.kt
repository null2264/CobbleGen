package dependencies

val minecraft = Dependency(
    group = "com.mojang",
    name = "minecraft",
    version = { mcVersion, hotfix ->
        when (mcVersion) {
            // For snapshots
            //12100 -> "some snapshot"
            else -> versionStr(mcVersion, hotfix)
        }
    },
)
