/*
 *  ******************************************************************************
 *  * Copyright (c) 2021 Deeplearning4j Contributors
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Apache License, Version 2.0 which is available at
 *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *  *****************************************************************************
 */
package org.deeplearning4j.scalnet.examples.keras.feedforward

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.deeplearning4j.scalnet.layers.core.Dense
import org.deeplearning4j.scalnet.logging.Logging
import org.deeplearning4j.scalnet.models.Sequential
import org.deeplearning4j.scalnet.regularizers.L2
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction

/**
  * Two-layer MLP for MNIST using keras-style Sequential
  * model construction pattern.
  *
  * @author David Kale
  */
object MLPMnistTwoLayerExample extends App with Logging {

  val height: Int = 28
  val width: Int = 28
  val nClasses: Int = 10
  val batchSize: Int = 64
  val hiddenSize = 512
  val seed: Int = 123
  val epochs: Int = 15
  val learningRate: Double = 0.0015
  val decay: Double = 0.005
  val scoreFrequency = 1000

  val mnistTrain: DataSetIterator = new MnistDataSetIterator(batchSize, true, seed)
  val mnistTest: DataSetIterator = new MnistDataSetIterator(batchSize, false, seed)

  logger.info("Build model...")
  val model: Sequential = Sequential(rngSeed = seed)

  model.add(Dense(hiddenSize, height * width, activation = Activation.RELU, regularizer = L2(learningRate * decay)))
  model.add(Dense(hiddenSize, activation = Activation.RELU, regularizer = L2(learningRate * decay)))
  model.add(Dense(nClasses, activation = Activation.SOFTMAX, regularizer = L2(learningRate * decay)))
  model.compile(LossFunction.NEGATIVELOGLIKELIHOOD)

  logger.info("Train model...")
  model.fit(mnistTrain, epochs, List(new ScoreIterationListener(scoreFrequency)))

  logger.info("Evaluate model...")
  logger.info(s"Train accuracy = ${model.evaluate(mnistTrain).accuracy}")
  logger.info(s"Test accuracy = ${model.evaluate(mnistTest).accuracy}")
}
