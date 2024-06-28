package com.example.dynamite.jsonGeneration

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move
import com.softwire.dynamite.game.Round
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File

@Serializable
data class GameMove(val p1: String, val p2: String)

@Serializable
data class GameMoves(val moves: List<GameMove>)

class LocalJsonGenerator {
    companion object {
        @JvmStatic
        fun createJson(playerBot: Bot, opponentBot: Bot, id: Int, logRounds: Boolean): String {
            val playerGameState = setupGameState()
            val opponentGameState = setupGameState()
            var playerScore = 0
            var opponentScore = 0
            var currentRoundWorth = 1

            val movesList = mutableListOf<GameMove>()

            while (true) {
                var playerMove: Move
                try {
                    playerMove = playerBot.makeMove(playerGameState)
                } catch (ex: Exception) {
                    break
                }

                var opponentMove: Move
                try {
                    opponentMove = opponentBot.makeMove(opponentGameState)
                } catch (ex: Exception) {
                    break
                }

                addRound(playerGameState, opponentGameState, playerMove, opponentMove)
                movesList.add(GameMove(playerMove.name, opponentMove.name))

                if (xWinsAgainstY(playerMove, opponentMove)) {
                    playerScore += currentRoundWorth
                    currentRoundWorth = 1
                } else if (xWinsAgainstY(opponentMove, playerMove)) {
                    opponentScore += currentRoundWorth
                    currentRoundWorth = 1
                } else {
                    ++currentRoundWorth
                }

                // Print summary of the current state of the game
                if(logRounds) {
                    println("Round ${playerGameState.rounds.size}:")
                    println("Player Move: $playerMove, Opponent Move: $opponentMove Player Score: $playerScore, Opponent Score: $opponentScore")
                    println("----------------------------")
                }
                if (playerScore >= 1000 || opponentScore >= 1000 || playerGameState.rounds.size >= 2500) {
                    break
                }
            }

            // Serialize movesList to JSON and write to file
            val gameMoves = GameMoves(movesList)
            val json = Json(JsonConfiguration.Stable)
            val jsonString = json.stringify(GameMoves.serializer(), gameMoves)

            // Construct the filename based on bot names
            val fileName = "GeneratedGames/game_moves_${id}.json"
            File(fileName).apply {
                parentFile.mkdirs() // Create directories if they do not exist
                writeText(jsonString)
            }
            return fileName
        }

        private fun xWinsAgainstY(x: Move, y: Move): Boolean {
            return if (x != Move.R || y != Move.S && y != Move.W) {
                if (x != Move.P || y != Move.R && y != Move.W) {
                    if (x == Move.S && (y == Move.P || y == Move.W)) {
                        true
                    } else if (x != Move.D || y != Move.R && y != Move.P && y != Move.S) {
                        x == Move.W && y == Move.D
                    } else {
                        true
                    }
                } else {
                    true
                }
            } else {
                true
            }
        }

        private fun setupGameState(): Gamestate {
            val playerGamestate = Gamestate()
            playerGamestate.setRounds(mutableListOf<Round>())
            return playerGamestate
        }

        private fun addRound(
            playerGamestate: Gamestate,
            opponentGamestate: Gamestate,
            playerMove: Move,
            opponentMove: Move
        ) {
            val playerRound = Round()
            playerRound.p1 = playerMove
            playerRound.p2 = opponentMove
            playerGamestate.rounds.add(playerRound)
            val opponentRound = Round()
            opponentRound.p1 = opponentMove
            opponentRound.p2 = playerMove
            opponentGamestate.rounds.add(opponentRound)
        }
    }

    interface Factory<T> {
        fun create(): T
    }
}