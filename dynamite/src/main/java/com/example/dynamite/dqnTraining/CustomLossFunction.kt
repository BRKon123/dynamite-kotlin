package com.example.dynamite.dqnTraining

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.activations.IActivation
import org.nd4j.linalg.lossfunctions.ILossFunction
import org.nd4j.common.primitives.Pair

class CustomLossFunction : ILossFunction {

    override fun computeScore(labels: INDArray, preOutput: INDArray, activationFn: IActivation, mask: INDArray?, average: Boolean): Double {
        val scoreArray = computeScoreArray(labels, preOutput, activationFn, mask)
        val score = scoreArray.sumNumber().toDouble()
        return if (average) score / scoreArray.size(0) else score
    }

    override fun computeScoreArray(labels: INDArray, preOutput: INDArray, activationFn: IActivation, mask: INDArray?): INDArray {
        val output = activationFn.getActivation(preOutput.dup(), true)
        val diff = labels.sub(output)
        val maskArray = labels.gt(0.0) // Create a mask where labels are greater than zero
        var scoreArr = diff.mul(diff).mul(maskArray) // Ensure diff is zero where labels are zero
        if (mask != null) {
            scoreArr = scoreArr.mulColumnVector(mask)
        }
        return scoreArr.sum(1)
    }

    override fun computeGradient(labels: INDArray, preOutput: INDArray, activationFn: IActivation, mask: INDArray?): INDArray {
        val output = activationFn.getActivation(preOutput.dup(), true)
        val diff = labels.sub(output)
        val maskArray = labels.gt(0.0) // Create a mask where labels are greater than zero
        var dLda = diff.mul(2).mul(maskArray) // Ensure diff is zero where labels are zero
        if (mask != null) {
            dLda = dLda.mulColumnVector(mask)
        }
        val gradients = activationFn.backprop(preOutput, dLda)
        var dLdz = gradients.first
        if (mask != null) {
            dLdz = dLdz.mulColumnVector(mask)
        }
        return dLdz
    }

    override fun computeGradientAndScore(labels: INDArray, preOutput: INDArray, activationFn: IActivation, mask: INDArray?, average: Boolean): Pair<Double, INDArray>? {
        val score = computeScore(labels, preOutput, activationFn, mask, average)
        val gradient = computeGradient(labels, preOutput, activationFn, mask)
        return Pair(score, gradient)
    }

    override fun name(): String {
        TODO("Not yet implemented")
    }

    fun activationFunctions(): List<IActivation> {
        return listOf()
    }
}
