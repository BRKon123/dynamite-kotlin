package com.example.dynamite

import com.example.dynamite.bots.DynamiteFirst
import com.example.dynamite.bots.Probabilistic
import com.example.dynamite.jsonGeneration.LocalJsonGenerator

fun main () {
    val playerBot = Probabilistic()
    val opponentBot= DynamiteFirst()
    LocalJsonGenerator.createJson(playerBot, opponentBot, 0, true)
}