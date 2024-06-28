package com.example.dynamite.experienceGeneration
import Experience
import ExperienceData
import com.example.dynamite.bots.BeatTheirPreviousMoveBot
import com.example.dynamite.bots.DynamiteFirst
import com.example.dynamite.bots.DynamiteOnDraw
import com.example.dynamite.bots.PaperBot
import com.example.dynamite.bots.Probabilistic
import com.example.dynamite.bots.RandomRPS
import com.example.dynamite.bots.RockBot
import com.example.dynamite.bots.ScissorsBot
import com.example.dynamite.jsonGeneration.LocalJsonGenerator
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File

object ExperienceGenerator {
    private val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
    private val bots = arrayOf(
        RockBot(),
        PaperBot(),
        ScissorsBot(),
        RandomRPS(),
        DynamiteFirst(),
        DynamiteOnDraw(),
        BeatTheirPreviousMoveBot(),
        Probabilistic()
    )

    @JvmStatic
    fun generateRandomExperiences(experienceFilePath: String, numGames: Int, lastNGames: Int, lastXAfterDraws: Int) {
        for (i in 0 until numGames) {
            val playerBot = bots.random()
            val opponentBot = bots.random()
            println("$playerBot vs $opponentBot, game $i")
            val gameMoveFilePath = LocalJsonGenerator.createJson(playerBot, opponentBot, 0, false)
            DQNFeatureExtractor.generateExperiencesJson(gameMoveFilePath, experienceFilePath, lastNGames, lastXAfterDraws)
        }
        println("Generated experiences for $numGames games and saved to $experienceFilePath")
    }


    @JvmStatic
    fun generateRandomExperiences(experienceFilePath: String, numGames: Int, lastNGames: Int, lastXAfterDraws: Int, batchSize: Int) {
        val experiences = mutableListOf<Experience>()

        for (i in 0 until numGames) {
            val playerBot = bots.random()
            val opponentBot = bots.random()
            val gameMoveFilePath = LocalJsonGenerator.createJson(playerBot, opponentBot, 0, false)
            val gameExperiences = DQNFeatureExtractor.extractFeaturesFromFile(gameMoveFilePath, lastNGames, lastXAfterDraws)
            experiences.addAll(gameExperiences.experiences)
            println("$playerBot vs $opponentBot, game $i")
            if (experiences.size >= batchSize) {
                println("writing now as batch size exceeded")
                writeExperiences(experienceFilePath, experiences)
                experiences.clear()
            }
        }

        // Write any remaining experiences
        if (experiences.isNotEmpty()) {
            writeExperiences(experienceFilePath, experiences)
        }

        println("Generated experiences for $numGames games and saved to $experienceFilePath")
    }

    private fun writeExperiences(outputFilePath: String, newExperiences: List<Experience>) {
        val experiences = if (File(outputFilePath).exists()) {
            val existingJson = File(outputFilePath).readText()
            val existingExperiences = json.parse(ExperienceData.serializer(), existingJson)
            existingExperiences.experiences.addAll(newExperiences)
            existingExperiences
        } else {
            ExperienceData(newExperiences.toMutableList())
        }

        val jsonString = json.stringify(ExperienceData.serializer(), experiences)

        // Write the JSON string to the specified output file
        File(outputFilePath).apply {
            parentFile.mkdirs() // Create directories if they do not exist
            writeText(jsonString)
        }

        println("Experiences added to file")
    }

}
