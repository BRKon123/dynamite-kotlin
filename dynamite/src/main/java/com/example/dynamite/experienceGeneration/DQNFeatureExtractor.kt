import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File

// Data classes to parse JSON input
@Serializable
data class GameMove(val p1: String, val p2: String)

@Serializable
data class GameData(val moves: List<GameMove>)

// Data classes to store experiences
@Serializable
data class Experience(
    val state: List<Float>,
    val action: Int,
    val reward: Float,
    val nextState: List<Float>,
    val done: Boolean
)

@Serializable
data class ExperienceData(
    val experiences: MutableList<Experience> = mutableListOf()
)

// Static class to process game data and generate experiences
class DQNFeatureExtractor private constructor() {
    companion object {
        private val moveSet = listOf("R", "P", "S", "D", "W", "N")
        private val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))

        @JvmStatic
        fun generateExperiencesJson(inputFilePath: String, outputFilePath: String, lastNGames: Int, lastXAfterDraws: Int) {
            val jsonText = File(inputFilePath).readText()
            val gameData = parseGameData(jsonText)
            val newExperiences = extractFeatures(gameData, lastNGames, lastXAfterDraws)
            val experiences = if (File(outputFilePath).exists()) {
                val existingJson = File(outputFilePath).readText()
                val existingExperiences = json.parse(ExperienceData.serializer(), existingJson)
                existingExperiences.experiences.addAll(newExperiences.experiences)
                existingExperiences
            } else {
                newExperiences
            }

            val jsonString = json.stringify(ExperienceData.serializer(), experiences)

            // Write the JSON string to the specified output file
            File(outputFilePath).apply {
                parentFile.mkdirs() // Create directories if they do not exist
                writeText(jsonString)
            }

            println("now added to file")
        }

        @JvmStatic
        fun extractFeaturesFromFile(inputFilePath: String, lastNGames: Int, lastXAfterDraws: Int): ExperienceData {
            val jsonText = File(inputFilePath).readText()
            val gameData = parseGameData(jsonText)
            return extractFeatures(gameData, lastNGames, lastXAfterDraws)
        }

        private fun parseGameData(jsonText: String): GameData {
            return json.parse(GameData.serializer(), jsonText)
        }

        private fun extractFeatures(gameData: GameData, lastNGames: Int, lastXAfterDraws: Int): ExperienceData {
            val experiences = mutableListOf<Experience>()
            val moves = gameData.moves
            var p1DynamiteLeft = 100
            var p2DynamiteLeft = 100
            var scoreDifference = 0
            var roundNumber = 0
            var roundPointValue = 1
            var p1Score = 0
            var p2Score = 0
            val lastDrawMoves = MutableList(lastXAfterDraws) { GameMove("N", "N") }
            val lastNMoves = MutableList(lastNGames) { GameMove("N", "N") }

            for (i in moves.indices) {
                roundNumber++

                // Extract features
                val state = extractState(
                    lastNMoves, lastDrawMoves, p1DynamiteLeft, p2DynamiteLeft,
                    scoreDifference, roundNumber, roundPointValue, p1Score, p2Score,
                    lastNGames, lastXAfterDraws
                )

                // Determine action and reward
                val action = determineAction(moves[i].p1)
                val reward = determineReward(moves[i], roundPointValue)
                val done = (i == moves.lastIndex)

                // Determine next state
                val nextState = if (i + 1 < moves.size) {
                    extractState(
                        lastNMoves, lastDrawMoves, p1DynamiteLeft, p2DynamiteLeft,
                        scoreDifference, roundNumber + 1, roundPointValue, p1Score, p2Score,
                        lastNGames, lastXAfterDraws
                    )
                } else { // if no next state then make it just all 0
                    List(state.size) { 0f }
                }

                // Update game variables
                if (moves[i].p1 == "D") p1DynamiteLeft--
                if (moves[i].p2 == "D") p2DynamiteLeft--

                // Adjust score based on reward
                if (reward > 0) {
                    scoreDifference += roundPointValue
                    p1Score += roundPointValue
                    roundPointValue = 1
                } else if (reward < 0) {
                    scoreDifference -= roundPointValue
                    p2Score += roundPointValue
                    roundPointValue = 1
                } else {
                    roundPointValue++
                }

                // Update last N moves
                lastNMoves.removeAt(0)
                lastNMoves.add(moves[i])

                // Add current move to draw list if the last move was a draw
                if (i > 0 && moves[i - 1].p1 == moves[i - 1].p2) {
                    lastDrawMoves.removeAt(0)
                    lastDrawMoves.add(moves[i])
                }

                experiences.add(Experience(state, action, reward, nextState, done))
            }

            return ExperienceData(experiences)
        }

        private fun extractState(
            lastNMoves: List<GameMove>,
            lastDrawMoves: List<GameMove>,
            p1DynamiteLeft: Int,
            p2DynamiteLeft: Int,
            scoreDifference: Int,
            roundNumber: Int,
            roundPointValue: Int,
            p1Score: Int,
            p2Score: Int,
            lastNGames: Int,
            lastXAfterDraws: Int
        ): List<Float> {
            val stateSize = lastNGames * moveSet.size * 2 + lastXAfterDraws * moveSet.size * 2 + 7
            val state = MutableList(stateSize) { 0f }

            // Fill last N games moves
            for (i in lastNMoves.indices) {
                encodeMove(lastNMoves[i].p1, state, i * moveSet.size * 2)
                encodeMove(lastNMoves[i].p2, state, i * moveSet.size * 2 + moveSet.size)
            }

            // Fill last X moves after draws
            for (i in lastDrawMoves.indices) {
                encodeMove(lastDrawMoves[i].p1, state, lastNGames * moveSet.size * 2 + i * moveSet.size * 2)
                encodeMove(lastDrawMoves[i].p2, state, lastNGames * moveSet.size * 2 + i * moveSet.size * 2 + moveSet.size)
            }

            // Add dynamite left, score difference, round number, round point value, and current scores
            val offset = lastNGames * moveSet.size * 2 + lastXAfterDraws * moveSet.size * 2
            state[offset] = p1DynamiteLeft.toFloat()
            state[offset + 1] = p2DynamiteLeft.toFloat()
            state[offset + 2] = scoreDifference.toFloat()
            state[offset + 3] = roundNumber.toFloat()
            state[offset + 4] = roundPointValue.toFloat()
            state[offset + 5] = p1Score.toFloat()
            state[offset + 6] = p2Score.toFloat()

            return state
        }

        private fun encodeMove(move: String, state: MutableList<Float>, startIndex: Int) {
            val index = moveSet.indexOf(move)
            if (index != -1) {
                state[startIndex + index] = 1f
            }
        }

        private fun determineAction(move: String): Int {
            return moveSet.indexOf(move)
        }

        private fun determineReward(move: GameMove, roundPointValue: Int): Float {
            return when {
                move.p1 == move.p2 -> 0f
                move.p1 == "D" && move.p2 == "W" -> -roundPointValue.toFloat()
                move.p1 == "W" && move.p2 == "D" -> roundPointValue.toFloat()
                move.p1 == "D" -> roundPointValue.toFloat()
                move.p2 == "D" -> -roundPointValue.toFloat()
                move.p1 == "R" && move.p2 == "S" -> roundPointValue.toFloat()
                move.p1 == "P" && move.p2 == "R" -> roundPointValue.toFloat()
                move.p1 == "S" && move.p2 == "P" -> roundPointValue.toFloat()
                move.p1 == "R" && move.p2 == "P" -> -roundPointValue.toFloat()
                move.p1 == "P" && move.p2 == "S" -> -roundPointValue.toFloat()
                move.p1 == "S" && move.p2 == "R" -> -roundPointValue.toFloat()
                else -> 0f
            }
        }

        @JvmStatic
        fun visualizeExperiences(inputFilePath: String, lastNGames: Int, lastXAfterDraws: Int) {
            val jsonText = File(inputFilePath).readText()
            val gameData = parseGameData(jsonText)
            val experiences = extractFeatures(gameData, lastNGames, lastXAfterDraws)

            for ((index, experience) in experiences.experiences.withIndex()) {
                println("Experience $index:")
                println("State: ")
                printState(experience.state, lastNGames, lastXAfterDraws)
                println("Action: ${moveSet[experience.action]}")
                println("Reward: ${experience.reward}")
                println("Next State: ")
                printState(experience.nextState, lastNGames, lastXAfterDraws)
                println("Done: ${experience.done}")
                println("------")
            }
        }

        private fun printState(state: List<Float>, lastNGames: Int, lastXAfterDraws: Int) {
            println("Last N Moves:")
            for (i in 0 until lastNGames) {
                val p1Move = decodeMove(state, i * moveSet.size * 2)
                val p2Move = decodeMove(state, i * moveSet.size * 2 + moveSet.size)
                println("  P1: $p1Move, P2: $p2Move")
            }
            println("Last X Moves After Draws:")
            for (i in 0 until lastXAfterDraws) {
                val p1Move = decodeMove(state, lastNGames * moveSet.size * 2 + i * moveSet.size * 2)
                val p2Move = decodeMove(state, lastNGames * moveSet.size * 2 + i * moveSet.size * 2 + moveSet.size)
                println("  P1: $p1Move, P2: $p2Move")
            }
            val offset = lastNGames * moveSet.size * 2 + lastXAfterDraws * moveSet.size * 2
            println("P1 Dynamite Left: ${state[offset]}")
            println("P2 Dynamite Left: ${state[offset + 1]}")
            println("Score Difference: ${state[offset + 2]}")
            println("Round Number: ${state[offset + 3]}")
            println("Round Point Value: ${state[offset + 4]}")
            println("P1 Current Score: ${state[offset + 5]}")
            println("P2 Current Score: ${state[offset + 6]}")
        }

        private fun decodeMove(state: List<Float>, startIndex: Int): String {
            for (i in moveSet.indices) {
                if (state[startIndex + i] == 1f) {
                    return moveSet[i]
                }
            }
            return "N"
        }
    }
}
