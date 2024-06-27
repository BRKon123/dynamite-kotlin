package com.example.dynamite.bots

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move
import kotlin.math.floor

class ProbabilisticBasic : Bot {
    private var dynamiteCount = 100
    private val myMoves = mutableListOf<Move>()

    init {
        // Are you debugging?
        // Put a breakpoint on the line below to see when we start a new match
        println("Started new match")
    }

    override fun makeMove(gamestate: Gamestate): Move {

        // Determine the next move
        val nextMove = when {
            shouldUseDynamite(gamestate.getRounds().size) -> {
                dynamiteCount--
                Move.D
            }
            else -> getRandomMove()
        }

        // Record my move
        myMoves.add(nextMove)
        return nextMove
    }

    private fun shouldUseDynamite(round: Int): Boolean {
        return dynamiteCount > 0 && floor(Math.random() * 100).toInt() < 10
    }


    private fun getRandomMove(): Move {
        val randomNumberBetween0And3 = floor(Math.random() * 3.0).toInt()
        val possibleMoves = arrayOf(Move.R, Move.P, Move.S)
        return possibleMoves[randomNumberBetween0And3]
    }
}
