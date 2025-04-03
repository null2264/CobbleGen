package dependencies

fun emi(loader: String? = null, api: Boolean = false) = Dependency(
    group = "dev.emi",
    name = if (loader != null) "emi-$loader" else "emi",
    version = { mcVersion ->
        buildString {
            if (mcVersion <= 11802) {
                append("0.7.3+${versionStr(mcVersion)}")  // There are no multi-loader support in 1.18.2
            } else {
                append("1.1.21+")
                append(
                    // They didn't break API on MC version upgrade so mismatch should be fine
                    when (mcVersion) {
                        in 11900..11902 -> "1.19.2"
                        in 11903..11904 -> versionStr(mcVersion)
                        in 12000..12001 -> "1.20.1"
                        12002 or 12004 -> "1.20.4"
                        12003 -> "1.20.2"
                        in 12005..12006 -> "1.20.6"
                        in 12100..12105 -> "1.21.1"
                        else -> throw IllegalStateException("$mcVersion is not yet supported!")
                    }
                )
            }
            if (api) append(":api")
        }
    },
)
