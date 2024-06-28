import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.parse
import java.io.File

object DataLoader {
    private val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))

    fun loadExperiences(filePath: String): List<Experience> {
        val jsonString = File(filePath).readText()
        val experienceData: ExperienceData = json.parse(ExperienceData.serializer(), jsonString)
        return experienceData.experiences
    }
}
