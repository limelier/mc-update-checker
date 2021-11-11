package mods

data class Mod(
    val name: String,
    val version: String,
    val loader: Loader,
) {
    enum class Loader {
        FORGE,
        FABRIC,
    }
}
