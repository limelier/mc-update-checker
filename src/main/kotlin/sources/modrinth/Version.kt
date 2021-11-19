package sources.modrinth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.InstantJsonSerializer
import java.time.Instant

@Serializable
class Version(
    @SerialName("game_versions")
    val gameVersions: List<String>,

    @SerialName("version_number")
    val versionNumber: String,

    @Serializable(InstantJsonSerializer::class)
    @SerialName("date_published")
    val datePublished: Instant,

    val loaders: List<String>,
)