package com.example.dynamite.bots

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move
import kotlin.math.floor

class Probabilistic : Bot {
    private var dynamiteCount = 100
    private val opponentMoves = mutableMapOf(
        Move.R to 0,
        Move.P to 0,
        Move.S to 0,
        Move.D to 0,
        Move.W to 0
    )
    private val myMoves = mutableListOf<Move>()

    init {
        // Are you debugging?
        // Put a breakpoint on the line below to see when we start a new match
        println("Started new match")
    }

    override fun makeMove(gamestate: Gamestate): Move {
        // Update opponent moves count
        if (gamestate.getRounds().isNotEmpty()) {
            val lastOpponentMove = gamestate.getRounds().last().p2
            opponentMoves[lastOpponentMove] = opponentMoves[lastOpponentMove]!! + 1
        }

        // Determine the next move
        val nextMove = when {
            shouldUseDynamite(gamestate.getRounds().size) -> Move.D
            shouldUseWaterBomb() -> Move.W
            else -> getBestCounterMove()
        }

        // Record my move
        myMoves.add(nextMove)
        return nextMove
    }

    private fun shouldUseDynamite(round: Int): Boolean {
        return dynamiteCount > 0 && floor(Math.random() * 100).toInt() < 10
    }

    private fun shouldUseWaterBomb(): Boolean {
        return (opponentMoves[Move.D] ?: 0) > 0 && floor(Math.random() * 1000).toInt() < 1
    }

    private fun getBestCounterMove(): Move {
        val mostFrequentMove = opponentMoves.entries.maxBy { it.value }?.key

        return when (mostFrequentMove) {
            Move.R -> Move.P
            Move.P -> Move.S
            Move.S -> Move.R
            Move.D -> Move.W
            else -> getRandomMove()
        }
    }

    private fun getRandomMove(): Move {
        val randomNumberBetween0And3 = floor(Math.random() * 3.0).toInt()
        val possibleMoves = arrayOf(Move.R, Move.P, Move.S)
        return possibleMoves[randomNumberBetween0And3]
    }
}
