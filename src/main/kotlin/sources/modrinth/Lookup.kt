package sources.modrinth

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import mods.Mod
import sources.Update
import java.net.URL

private const val apiBaseUrl = "https://api.modrinth.com/api/v1/mod"
private const val normalBaseUrl = "https://modrinth.com/mod"

private val json = Json { ignoreUnknownKeys = true }

private fun findModId(mod: Mod, minecraftVersion: String): String? {
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

    return searchResult.hits[0].modId.substringAfter("local-")
}

fun modrinthLookup(mod: Mod, minecraftVersion: String): Update? {
    val modId = findModId(mod, minecraftVersion) ?: return null

    val (_, _, modQueryResult) = Fuel
        .get("${apiBaseUrl}/${modId}/version")
        .responseString()
    if (modQueryResult is Result.Failure) return null
    val versions = json.decodeFromString<List<Version>>(modQueryResult.get())
    val compatibleVersions = versions.filter {
        it.gameVersions.contains(minecraftVersion) && it.loaders.contains(mod.loader.toString().lowercase())
    }

    val thisVersion = compatibleVersions.firstOrNull { it.versionNumber == mod.version } ?: return null
    val latestVersion = compatibleVersions.maxByOrNull { it.datePublished } ?: return null

    if (latestVersion.datePublished > thisVersion.datePublished) {
        return Update(mod, URL("${normalBaseUrl}/${modId}"), latestVersion.versionNumber)
    }

    return null
}