import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.constraints.toConstraint
import io.github.z4kn4fein.semver.toVersion
import io.github.z4kn4fein.semver.toVersionOrNull
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.reflect.typeOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * @param upper No longer supported version
 * @param lower Last supported version
 */
data class VersionRange(
    val from: String?,
    val to: String?,
    val inclusiveFrom: Boolean = false,
    val inclusiveTo: Boolean = true,
    val isSnapshot: Boolean = false,
) {
    val fromSanitized: String? get() = from?.replace(".x", ".9999", true)
    val toSanitized: String? get() = to?.replace(".x", ".9999", true)

    fun mavenStyle(): String {
        require(from != null || to != null) { "'from' and 'to' can't be all null. Update supportedVersionRange before proceeding!" }

        if (from != null && to != null && from == to) return "[${from}]"

        return buildString {
            append(if (inclusiveFrom) "[" else ")")
            if (from != null) append(fromSanitized)
            append(",")
            if (to != null) append(toSanitized)
            append(if (inclusiveTo) "]" else ")")
        }
    }

    fun semverStyle(): String {
        require(from != null || to != null) { "'from' and 'to' can't be all null. Update supportedVersionRange before proceeding!" }

        if (from != null && to != null && from == to) return from

        return buildString {
            if (from != null) {
                append(if (inclusiveFrom) ">=" else ">")
                append(fromSanitized)
            }
            if (to != null) {
                append(" ")
                append(if (inclusiveTo) "<=" else "<")
                append(toSanitized)
            }
        }
    }
}

fun supportedVersionRange(mcVersion: Int, loader: String): VersionRange {
    return when (mcVersion) {
        11605 -> VersionRange("1.16.5", "1.16.5")
        11802 -> VersionRange("1.18.2", "1.18.2")
        in 11900..11902 -> VersionRange("1.18.x", "1.19.2")
        in 11903..11904 -> VersionRange("1.19.2", "1.19.4")
        in 12000..12001 -> VersionRange("1.19.x", "1.20.1")
        in 12002..12003 -> VersionRange("1.20.1", if (loader != "neoforge") "1.20.4" else "1.20.3")
        12004 -> VersionRange("1.20.3", "1.20.4")  // for Neo
        in 12005..12006 -> VersionRange("1.20.4", "1.20.6")
        in 12100..12101 -> VersionRange("1.20.x", "1.21.1")
        in 12102..12104 -> VersionRange("1.21.1", "1.21.4")
        in 12105..12110 -> VersionRange("1.21.4", "1.21.10")
        12111 -> VersionRange("1.21.10", "1.21.11")
        260100 -> VersionRange("1.21.11", null, isSnapshot = true)
        else -> VersionRange(null, null)
    }
}

@Serializable
data class MojangVersionManifest(
    val versions: List<MinecraftVersion>,
) {
    @Serializable
    data class MinecraftVersion(
        val id: String,
        val type: String,
    )
}

fun Version.toMojangString(): String =
    "$major.$minor${if (patch > 0) ".$patch" else ""}${preRelease?.let { "-$preRelease" } ?: ""}" +
        (buildMetadata?.let { "+$buildMetadata" } ?: "")

fun mcVersions(target: VersionRange): List<Version> {
    val filters = buildList {
        add("release")
        if (target.isSnapshot) add("snapshot")
    }

    val client = HttpClient.newHttpClient()
    // TODO: Caching this is probably a good idea...
    val response = client.send(
        HttpRequest.newBuilder().GET().uri(URI("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json")).build(),
        HttpResponse.BodyHandlers.ofString(),
    )
    val data = lenientJson.decodeFromString<MojangVersionManifest>(response.body())
    return data.versions.mapNotNull map@{
        if (it.type !in filters) return@map null
        val version = it.id.toVersionOrNull(false) ?: return@map null
        if (!target.semverStyle().toConstraint().isSatisfiedBy(version)) return@map null

        return@map version
    }
}
