package sources

import mods.Mod
import java.net.URL

class Update(
    val mod: Mod,
    val source: URL,
    val version: String,
)