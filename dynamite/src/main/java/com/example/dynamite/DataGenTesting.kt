package com.example.dynamite

import DQNFeatureExtractor
import com.example.dynamite.bots.DynamiteOnDraw
import com.example.dynamite.bots.PaperBot
import com.example.dynamite.bots.Probabilistic
import com.example.dynamite.bots.RockBot
import com.example.dynamite.bots.ScissorsBot
import com.example.dynamite.jsonGeneration.LocalJsonGenerator
import com.example.dynamite.bots.DynamiteFirst
import com.example.dynamite.bots.BeatTheirPreviousMoveBot
import com.example.dynamite.bots.RandomRPS


fun main () {
    val bots = arrayOf(
        RockBot(),
        PaperBot(),
        ScissorsBot(),
        RandomRPS(),
        DynamiteFirst(),
        DynamiteOnDraw(),
        BeatTheirPreviousMoveBot()
    )
    val playerBot = Probabilistic()
    val opponentBot= Probabilistic()
    val gameMoveFilePath = LocalJsonGenerator.createJson(playerBot, opponentBot, 0, false)

    val experienceFilePath = "ModelData/experiences.json"
    val lastNGames = 10
    val lastXAfterDraws = 5
    DQNFeatureExtractor.generateExperiencesJson(gameMoveFilePath, experienceFilePath, lastNGames, lastXAfterDraws)
    println("Experiences JSON has been generated and saved to $experienceFilePath")
    DQNFeatureExtractor.visualizeExperiences(gameMoveFilePath, lastNGames, lastXAfterDraws)
}