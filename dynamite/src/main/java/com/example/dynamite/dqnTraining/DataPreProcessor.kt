package com.example.dynamite.dqnTraining

import Experience
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork

object DataPreprocessor {
    fun preprocessExperiences(network: MultiLayerNetwork, experiences: List<Experience>): List<Pair<INDArray, INDArray>> {
        val states = mutableListOf<INDArray>()
        val targets = mutableListOf<INDArray>()

        for (experience in experiences) {
            val stateArray = Nd4j.create(experience.state.map { it.toDouble() }.toDoubleArray())
            val nextStateArray = Nd4j.create(experience.nextState.map { it.toDouble() }.toDoubleArray())

            // Predict Q-values for the next state
            val targetQValues = network.output(nextStateArray, false)

            // Calculate the target Q-value for the action taken
            val target = experience.reward + if (!experience.done) 0.99 * targetQValues.maxNumber().toDouble() else 0.0

            // Predict Q-values for the current state
            val targetArray = network.output(stateArray, false)

            // Update the Q-value for the specific action taken
            targetArray.putScalar(intArrayOf(experience.action), target)

            // Add to the list of states and targets
            states.add(stateArray)
            targets.add(targetArray)
        }

        return states.zip(targets)
    }
}
