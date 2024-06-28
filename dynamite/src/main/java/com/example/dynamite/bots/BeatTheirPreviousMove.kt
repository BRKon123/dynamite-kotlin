package com.example.dynamite.bots

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move
import com.softwire.dynamite.game.Round
import kotlin.math.floor

class BeatTheirPreviousMoveBot : Bot {
    override fun makeMove(gamestate: Gamestate): Move {
        if (gamestate.rounds.size == 0) {
            return this.randomMove
        } else {
            val lastRound = gamestate.rounds[gamestate.rounds.size - 1] as Round
            return this.getMoveThatBeats(lastRound.p2)
        }
    }

    private fun getMoveThatBeats(theirLastMove: Move): Move {
        return when (theirLastMove) {
            Move.R -> Move.P
            Move.P -> Move.S
            Move.S -> Move.R
            Move.D -> Move.W
            Move.W -> Move.R
            else -> throw RuntimeException("Invalid last move from P2")
        }
    }

    val randomMove: Move
        get() {
            val randomNumberBetween0And3 = floor(Math.random() * 3.0).toInt()
            val possibleMoves = arrayOf(Move.R, Move.P, Move.S)
            val randomMove = possibleMoves[randomNumberBetween0And3]
            return randomMove
        }
}