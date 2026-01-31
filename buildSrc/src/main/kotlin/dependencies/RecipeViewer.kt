package dependencies

import CGVer

fun emi(mcVersion: CGVer, loader: String? = null, api: Boolean = false) = Dependency(
    group = "dev.emi",
    name = if (loader != null) {
        // EMI migrate to NeoForge after 1.20.2
        if (loader != "fabric" && mcVersion.code <= 12002) "emi-forge" else "emi-$loader"
    } else "emi",
    version = { _ ->
        buildString {
            if (mcVersion.code <= 11802) {
                append("0.7.3+$mcVersion")  // There are no multi-loader support in 1.18.2
            } else {
                append("1.1.21+")
                append(
                    // They didn't break API on MC version upgrade so mismatch should be fine
                    when (mcVersion.code) {
                        in 11900..11902 -> "1.19.2"
                        in 11903..11904 -> mcVersion.string
                        in 12000..12001 -> "1.20.1"
                        12002, 12004 -> mcVersion.string
                        12003 -> "1.20.2"
                        in 12005..12006 -> "1.20.6"
                        in 12100..12111 -> "1.21.1"
                        260100 -> "1.21.1"  // FIXME: Not confirmed, but EMI might skip 1.21.11 for 26.1
                        else -> throw IllegalStateException("$mcVersion is not yet supported!")
                    }
                )
            }
            if (api) append(":api")
        }
    },
)

fun rei(loader: String, api: Boolean = false) = Dependency(
    group = "me.shedaniel",
    name = if (loader == "fabric" && api) {
        "RoughlyEnoughItems-api-$loader"
    } else {
        "RoughlyEnoughItems-$loader"
    },
    version = { mcVersion ->
        // They didn't break API on MC version upgrade so mismatch should be fine
        when (mcVersion.code) {
            11802 -> "8.3.618"
            in 11900..11902 -> "9.1.619"
            in 11903..11904 -> "11.0.621"
            in 12000..12001 -> "12.0.625"
            in 12002..12004 -> "13.0.685"
            in 12005..12006 -> "15.0.787"
            in 12100..12101 -> "16.0.788"
            in 12102..12110 -> "17.0.789"
            12111 -> "17.0.789"  // FIXME: Broken in 1.21.11, waiting for new release
            260100 -> "17.0.789"
            else -> throw IllegalStateException("$mcVersion is not yet supported!")
        }
    },
)

fun jei(mcVersion: CGVer, loader: String, common: Boolean = false, api: Boolean = false) = Dependency(
    group = "mezz.jei",
    name = buildString {
        append("jei-")
        append(
            // They didn't break API on MC version upgrade so mismatch should be fine
            when (mcVersion.code) {
                11802 -> mcVersion.string
                in 11900..11903 -> "1.19.2"
                11904 -> mcVersion.string
                in 12000..12001 -> "1.20.1"
                in 12002..12004 -> "1.20.2"
                in 12005..12006 -> mcVersion.string
                in 12100..12110 -> "1.21.1"
                12111 -> "1.21.11"
                260100 -> "1.21.11"
                else -> throw IllegalStateException("$mcVersion is not yet supported!")
            }
        )
        if (api) {
            if (common) {
                append("-common")
            } else {
                append("-")
                if (loader != "fabric" && mcVersion.code < 12100) append("forge")
                else append(loader)
            }
            append ("-api")
        }
    },
    version = { _ ->
        // They didn't break API on MC version upgrade so mismatch should be fine
        when (mcVersion.code) {
            11802 -> "10.2.1.1009"
            in 11900..11903 -> "11.8.1.1034"
            11904 -> "13.1.0.13"
            in 12000..12001 -> "15.20.0.106"
            in 12002..12004 -> "16.0.0.28"
            in 12005..12006 -> "18.0.0.62"
            in 12100..12110 -> "19.21.1.248"
            12111 -> "27.3.0.14"
            260100 -> "27.3.0.14"
            else -> throw IllegalStateException("$mcVersion is not yet supported!")
        }
    },
)
