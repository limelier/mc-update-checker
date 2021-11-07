package mods

import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarFile
import kotlin.io.path.isDirectory

fun processMods(modsDir: Path): List<Mod> {
    if (!modsDir.isDirectory()) {
        println("Could not find mods directory")
        return listOf()
    }

    return Files
        .find(modsDir, 1, {_, fileAttr -> fileAttr.isRegularFile })
        .map { path ->
            processMod(JarFile(path.toFile()))
        }
        .toList()
}


private fun processMod(jarFile: JarFile): Mod {
    val fabricModJsonEntry = jarFile.getJarEntry("fabric.mod.json")

    return if (fabricModJsonEntry == null) {
        processForgeMod(jarFile)
    } else {
        processFabricMod(jarFile, fabricModJsonEntry)
    }
}