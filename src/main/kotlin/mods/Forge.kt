package mods

import com.moandjiezana.toml.Toml
import kotlinx.serialization.Serializable
import java.util.jar.JarFile


@Serializable
private class ModsToml(val mods: Array<ModEntry>) {
    @Serializable
    class ModEntry(val displayName: String, val version: String)
}

fun processForgeMod(jarFile: JarFile): Mod {
    val modsTomlEntry = jarFile.getJarEntry("META-INF/mods.toml")

    return if (modsTomlEntry != null) {
        val modsToml = Toml().read(jarFile.getInputStream(modsTomlEntry))
        val name = modsToml.getString("mods[0].displayName")
        val version = modsToml.getString("mods[0].version")

        Mod(name, version, Mod.Loader.FORGE)
    } else {
        val attribs = jarFile.manifest.mainAttributes
        val name = attribs.getValue("Implementation-Title")
            ?: attribs.getValue("Specification-Title")
            ?: ""
        val version = attribs.getValue("Implementation-Version") ?: ""

        Mod(name, version, Mod.Loader.FORGE)
    }

}