import com.example.dynamite.dqnTraining.DataPreprocessor
import com.example.dynamite.dqnTraining.NetworkBuilder
import com.example.dynamite.dqnTraining.Trainer


object DynamiteQlearning {
    private val lastNGames = 10
    private val lastXAfterDraws = 5

    @JvmStatic
    fun main(args: Array<String>) {
        val stateSize = lastNGames * 6 * 2 + lastXAfterDraws * 6 * 2 + 7
        val numActions = 5   // Number of actions in the game (Rock, Paper, Scissors, Dynamite, Water Bomb)

        val network = NetworkBuilder.buildNetwork(stateSize, numActions)
        val experiences = DataLoader.loadExperiences("ModelData/experiences.json")
        val trainingData = DataPreprocessor.preprocessExperiences(network, experiences, numActions)

        Trainer.train(network, trainingData, epochs = 100, batchSize = 64)

        println("Training completed")
    }
}
