package com.example.dynamite

import com.example.dynamite.bots.DynamiteFirst
import com.example.dynamite.bots.DynamiteOnDraw
import com.example.dynamite.bots.Probabilistic
import com.example.dynamite.bots.RockBot
import com.example.dynamite.bots.ScissorsBot
import com.example.dynamite.jsonGeneration.LocalJsonGenerator

fun main () {
    val playerBot = Probabilistic()
    val opponentBot= DynamiteOnDraw()
    val gameMoveFilePath = LocalJsonGenerator.createJson(playerBot, opponentBot, 0, false)

    //DataJsonGenerator.generateJson(gameMoveFilePath)
}