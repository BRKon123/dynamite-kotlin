package com.example.dynamite.bots

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move
import com.softwire.dynamite.game.Round
import kotlin.math.floor


class DynamiteOnDraw : Bot {
    override fun makeMove(gamestate: Gamestate): Move {
        if (this.numberOfDynamitesPlayed(gamestate) == 100) {
            return this.randomMove
        } else if (gamestate.rounds.size == 0) {
            return this.randomMove
        } else {
            val lastRound = gamestate.rounds[gamestate.rounds.size - 1] as Round
            return if (lastRound.p1 == lastRound.p2) Move.D else randomMove
        }
    }

    private fun numberOfDynamitesPlayed(gamestate: Gamestate): Int {
        var dynamites = 0
        val var3: Iterator<*> = gamestate.rounds.iterator()

        while (var3.hasNext()) {
            val round = var3.next() as Round
            if (round.p1 == Move.D) {
                ++dynamites
            }
        }

        return dynamites
    }

    val randomMove: Move
        get() {
            val randomNumberBetween0And3 = floor(Math.random() * 3.0).toInt()
            val possibleMoves = arrayOf(Move.R, Move.P, Move.S)
            val randomMove = possibleMoves[randomNumberBetween0And3]
            return randomMove
        }
}
