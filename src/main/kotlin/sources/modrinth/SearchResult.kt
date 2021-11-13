package sources.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SearchResult(
    val hits: List<ModResult>
) {
    @Serializable
    class ModResult(
        @SerialName("mod_id")
        val modId: String
    )
}