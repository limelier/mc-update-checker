package mods

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.jar.JarEntry
import java.util.jar.JarFile

@Serializable
private class FabricModJson(val name: String, val version: String)

private val json = Json { ignoreUnknownKeys = true }

fun processFabricMod(jarFile: JarFile, fabricModJsonEntry: JarEntry): Mod {
    val fabricModJsonString = jarFile.getInputStream(fabricModJsonEntry).bufferedReader().use { it.readText() }
    val fabricModJson = json.decodeFromString<FabricModJson>(fabricModJsonString)

    return Mod(fabricModJson.name, fabricModJson.version)
}