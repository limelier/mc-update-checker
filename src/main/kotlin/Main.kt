import mods.processMods
import sources.modrinth.modrinthLookup

fun main() {
    val rootPath = getMinecraftDirectory()
    val minecraftVersion = getMinecraftVersion()

    val mods = processMods(rootPath.resolve("mods"))
    val updates = mods.mapNotNull { modrinthLookup(it, minecraftVersion) }

    for (update in updates) {
        println("""
            Update available for ${update.mod.name}
                local: ${update.mod.version}
                remote: ${update.version}
                ${update.source}
        """.trimIndent())
    }
}