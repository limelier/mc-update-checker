package sources

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import mods.Mod
import java.net.URL
import java.time.Instant

private const val apiBaseUrl = "https://api.modrinth.com/api/v1/mod"
private const val normalBaseUrl = "https://modrinth.com/mod"

@Serializable
private class SearchResult(
    val hits: List<ModResult>
) {
    @Serializable
    class ModResult(
        @SerialName("mod_id")
        val modId: String
    )
}

object InstantSerializer : KSerializer<Instant> {
    override fun deserialize(decoder: Decoder): Instant {
        val str = decoder.decodeString()
        return Instant.parse(str)
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }
}

@Serializable
private class Version(
    @SerialName("game_versions")
    val gameVersions: List<String>,

    @SerialName("version_number")
    val versionNumber: String,

    @Serializable(InstantSerializer::class)
    @SerialName("date_published")
    val datePublished: Instant
)

private val json = Json { ignoreUnknownKeys = true }

fun modrinthLookup(mod: Mod, minecraftVersion: String): Update? {
    val facets = Json.encodeToString(listOf(
        listOf("categories:${mod.loader.toString().lowercase()}"),
        listOf("versions:${minecraftVersion}")
    ))
    val (_, _, searchQueryResult) = Fuel
        .get(apiBaseUrl, listOf("query" to mod.name, "facets" to facets))
        .responseString()

    if (searchQueryResult is Result.Failure) return null
    val searchResult = json.decodeFromString<SearchResult>(searchQueryResult.get())
    if (searchResult.hits.isEmpty()) return null

    if (searchResult.hits.size > 1) {
        println("<!> There's more than one hit for ${mod.name}, picking the first for now") // todo give user choice
    }

    val modId = searchResult.hits[0].modId.substringAfter("local-")

    val (_, _, modQueryResult) = Fuel
        .get("${apiBaseUrl}/${modId}/version")
        .responseString()
    if (modQueryResult is Result.Failure) return null
    val versions = json.decodeFromString<List<Version>>(modQueryResult.get())
    val compatibleVersions = versions.filter { it.gameVersions.contains(minecraftVersion) } // todo filter for loader too

    val thisVersion = compatibleVersions.firstOrNull { it.versionNumber == mod.version } ?: return null
    val latestVersion = compatibleVersions.maxByOrNull { it.datePublished } ?: return null

    if (latestVersion.datePublished > thisVersion.datePublished) {
        return Update(mod, URL("${normalBaseUrl}/${modId}"), latestVersion.versionNumber)
    }

    return null
}