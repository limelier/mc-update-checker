import mods.processMods

fun main() {
    val rootPath = getMinecraftDirectory()

    for (mod in processMods(rootPath.resolve("mods"))) {
        println(mod)
    }
}