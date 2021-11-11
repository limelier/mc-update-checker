import java.nio.file.Path
import kotlin.io.path.isDirectory

fun getMinecraftVersion(): String {
    print("Please input the minecraft version (e.g.: 1.17.1): ")
    return readLine()!!
}

fun getMinecraftDirectory(): Path {
    while (true) {
        print("Please input the path to your .minecraft directory: ")
        val path = Path.of(readLine()!!)

        if (!path.isDirectory()) {
            println("Not a directory")
            continue
        }

        return path
    }
}