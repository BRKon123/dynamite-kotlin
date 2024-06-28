package com.example.dynamite

import com.example.dynamite.experienceGeneration.ExperienceGenerator



fun main() {
    val experienceFilePath = "ModelData/experiences.json"
    val numGames = 200
    val batchSize = 100000 // Adjust the batch size based on memory considerations
    val lastNGames = 10
    val lastXAfterDraws = 5

    ExperienceGenerator.generateRandomExperiences(experienceFilePath, numGames, lastNGames, lastXAfterDraws, batchSize)
}