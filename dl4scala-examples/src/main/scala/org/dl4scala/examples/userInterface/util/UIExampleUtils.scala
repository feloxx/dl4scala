package org.dl4scala.examples.userInterface.util

import java.io.IOException

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.inputs.InputType
import org.deeplearning4j.nn.conf.layers.{ConvolutionLayer, DenseLayer, OutputLayer, SubsamplingLayer}
import org.deeplearning4j.nn.conf.{NeuralNetConfiguration, Updater}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.lossfunctions.LossFunctions

/**
  * Created by endy on 2017/6/25.
  */
object UIExampleUtils {

  def getMnistNetwork: MultiLayerNetwork = {
    val nChannels = 1 // Number of input channels
    val outputNum = 10 // The number of possible outcomes
    val iterations = 1 // Number of training iterations
    val seed = 123 //

    val conf = new NeuralNetConfiguration.Builder()
      .seed(seed)
      .iterations(iterations) // Training iterations as above
      .regularization(true).l2(0.0005)
      .learningRate(0.01)
      .weightInit(WeightInit.XAVIER)
      .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
      .updater(Updater.NESTEROVS)
      .list()
      .layer(0, new ConvolutionLayer.Builder(5, 5)
        //nIn and nOut specify depth. nIn here is the nChannels and nOut is the number of filters to be applied
        .nIn(nChannels)
        .stride(1, 1)
        .nOut(20)
        .activation(Activation.LEAKYRELU)
        .build())
      .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
        .kernelSize(2,2)
        .stride(2,2)
        .build())
      .layer(2, new ConvolutionLayer.Builder(5, 5)
        //Note that nIn need not be specified in later layers
        .stride(1, 1)
        .nOut(50)
        .activation(Activation.LEAKYRELU)
        .build())
      .layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
        .kernelSize(2,2)
        .stride(2,2)
        .build())
      .layer(4, new DenseLayer.Builder().activation(Activation.LEAKYRELU).nOut(500).build())
      .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
        .nOut(outputNum)
        .activation(Activation.SOFTMAX)
        .build())
      .setInputType(InputType.convolutionalFlat(28,28,1))
      .backprop(true).pretrain(false).build()

    val net = new MultiLayerNetwork(conf)
    net.init()

    net
  }

  def getMnistData: DataSetIterator = try
    new MnistDataSetIterator(64, true, 12345)
  catch {
    case e: IOException =>
      throw new RuntimeException(e)
  }
}
