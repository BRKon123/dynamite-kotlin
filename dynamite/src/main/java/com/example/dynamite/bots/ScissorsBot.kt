package com.example.dynamite.bots

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move

class ScissorsBot : Bot {
    override fun makeMove(gamestate: Gamestate): Move {
        return Move.S
    }
}
