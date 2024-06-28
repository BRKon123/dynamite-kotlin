package com.example.dynamite.dqnTraining
import Experience
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.factory.Nd4j

object Trainer {
    fun train(network: MultiLayerNetwork, trainingData: List<Pair<INDArray, INDArray>>, epochs: Int, batchSize: Int) {

        for (epoch in 1..epochs) {
            trainingData.chunked(batchSize).forEach { batch ->
                val states = Nd4j.vstack(*batch.map { it.first }.toTypedArray())
                val targets = Nd4j.vstack(*batch.map { it.second }.toTypedArray())

                val dataSet = DataSet(states, targets)
                network.fit(dataSet)
            }
            println("Epoch $epoch completed")
        }
    }
}
