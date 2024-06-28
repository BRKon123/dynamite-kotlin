package com.example.dynamite.bots

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move
import kotlin.math.floor

class RandomRPS : Bot {
    override fun makeMove(gamestate: Gamestate): Move {
        val randomNumberBetween0And3 = floor(Math.random() * 3.0).toInt()
        val possibleMoves = arrayOf(Move.R, Move.P, Move.S)
        val randomMove = possibleMoves[randomNumberBetween0And3]
        return randomMove
    }
}
