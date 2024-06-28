package com.example.dynamite.dqnTraining

import Experience
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork

object DataPreprocessor {
    private const val MAX_DYNAMITE = 100.0
    private const val MAX_SCORE_DIFFERENCE = 100.0
    private const val MAX_ROUND_NUMBER = 2500.0
    private const val MAX_ROUND_POINT_VALUE = 10.0
    private const val MAX_SCORE = 1000.0

    private fun normalizeState(state: List<Float>): List<Float> {
        val normalizedState = state.toMutableList()

        // Assuming the structure of the state array and applying normalization
        val offset = state.size - 7
        normalizedState[offset] = state[offset] / MAX_DYNAMITE.toFloat()
        normalizedState[offset + 1] = state[offset + 1] / MAX_DYNAMITE.toFloat()
        normalizedState[offset + 2] = state[offset + 2] / MAX_SCORE_DIFFERENCE.toFloat()
        normalizedState[offset + 3] = state[offset + 3] / MAX_ROUND_NUMBER.toFloat()
        normalizedState[offset + 4] = state[offset + 4] / MAX_ROUND_POINT_VALUE.toFloat()
        normalizedState[offset + 5] = state[offset + 5] / MAX_SCORE.toFloat()
        normalizedState[offset + 6] = state[offset + 6] / MAX_SCORE.toFloat()

        return normalizedState
    }

    fun preprocessExperiences(network: MultiLayerNetwork, experiences: List<Experience>, numActions: Int): List<Pair<INDArray, INDArray>> {
        val states = mutableListOf<INDArray>()
        val targets = mutableListOf<INDArray>()

        for (experience in experiences) {
            val normalizedState = normalizeState(experience.state).map { it.toDouble() }
            val normalizedNextState = normalizeState(experience.nextState).map { it.toDouble() }

            val stateArray = Nd4j.create(normalizedState.toDoubleArray())

            // Calculate the target Q-value for the action taken
            val target = experience.reward

            // Initialize target array with zeros, shape based on the number of actions
            val targetArray = Nd4j.zeros(numActions)

            // Set the target value for the action taken
            targetArray.putScalar(intArrayOf(experience.action), target)

            // Add to the list of states and targets
            states.add(stateArray)
            targets.add(targetArray)
        }

        return states.zip(targets)
    }
}



