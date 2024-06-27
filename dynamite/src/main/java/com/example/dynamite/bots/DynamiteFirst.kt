package com.example.dynamite.bots

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move
import kotlin.math.floor

class DynamiteFirst : Bot {

    init {
        // Are you debugging?
        // Put a breakpoint on the line below to see when we start a new match
        println("Started new match")
    }

    override fun makeMove(gamestate: Gamestate): Move {
        // Are you debugging?
        // Put a breakpoint in this method to see when we make a move
        return if (gamestate.rounds.size < 100) Move.D else this.getRandomMove()
    }

    fun getRandomMove(): Move {
        val randomNumberBetween0And3 = floor(Math.random() * 3.0).toInt()
        val possibleMoves = arrayOf(Move.R, Move.P, Move.S)
        val randomMove = possibleMoves[randomNumberBetween0And3]
        return randomMove
    }
}