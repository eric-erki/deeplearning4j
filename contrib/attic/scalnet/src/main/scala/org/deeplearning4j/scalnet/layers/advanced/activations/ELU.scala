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
package org.deeplearning4j.scalnet.layers.advanced.activations

import org.deeplearning4j.nn.conf.layers.{ ActivationLayer => JActivationLayer }
import org.deeplearning4j.scalnet.layers.core.Layer
import org.nd4j.linalg.activations.impl.{ ActivationELU }

/**
  * ELU layer
  *
  * @author Max Pumperla
  */
class ELU(alpha: Double, nOut: Option[List[Int]], nIn: Option[List[Int]], override val name: String = "")
    extends Layer {

  override def compile: org.deeplearning4j.nn.conf.layers.Layer =
    new JActivationLayer.Builder()
      .activation(new ActivationELU(alpha))
      .name(name)
      .build()

  override val outputShape: List[Int] = nOut.getOrElse(List(0))
  override val inputShape: List[Int] = nIn.getOrElse(List(0))

  override def reshapeInput(newIn: List[Int]): ELU =
    new ELU(alpha, Some(newIn), Some(newIn), name)
}

object ELU {
  def apply(alpha: Double, nOut: Int = 0, nIn: Int = 0, name: String = ""): ELU =
    new ELU(alpha, Some(List(nOut)), Some(List(nIn)), name)
}
