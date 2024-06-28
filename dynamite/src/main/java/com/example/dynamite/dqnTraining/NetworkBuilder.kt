package com.example.dynamite.dqnTraining

import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.Adam
import org.nd4j.linalg.lossfunctions.LossFunctions

object NetworkBuilder {
    fun buildNetwork(stateSize: Int, numActions: Int): MultiLayerNetwork {
        val conf = NeuralNetConfiguration.Builder()
            .updater(Adam(0.001))
            .list()
            .layer(DenseLayer.Builder().nIn(stateSize).nOut(128).activation(Activation.RELU).build())
            .layer(DenseLayer.Builder().nIn(128).nOut(128).activation(Activation.RELU).build())
            .layer(OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .activation(Activation.IDENTITY)
                .nIn(128).nOut(numActions).build())
            .build()

        val net = MultiLayerNetwork(conf)
        net.init()
        net.setListeners(ScoreIterationListener(10))
        return net
    }
}
