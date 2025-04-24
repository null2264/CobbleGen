package dependencies

fun createMod(isNeo: Boolean) = Dependency(
    group = "com.simibubi.create",
    // Create finally support Neo on 1.21.1
    name = "create" + (if (!isNeo) "-1.18.2" else "-1.21.1"),
    version = { mcVersion ->
        val version = if (!isNeo) "0.5.1.e-318" else "6.0.4-59"
        return@Dependency "$version:slim"
    },
)
