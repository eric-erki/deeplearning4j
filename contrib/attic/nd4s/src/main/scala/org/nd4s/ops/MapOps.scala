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
package org.nd4s.ops

import org.nd4j.autodiff.samediff.SDVariable
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.ops.BaseScalarOp
import org.nd4s.Implicits._

object MapOps {
  def apply(x: INDArray, f: Double => Double): MapOps = new MapOps(x, f)
}
class MapOps(_x: INDArray, f: Double => Double) extends BaseScalarOp(_x, null, _x, 0) with LeftAssociativeBinaryOp {
  x = _x
  def this() {
    this(0.toScalar, null)
  }

  override def opNum(): Int = -1

  override def opName(): String = "map_scalar"

  override def onnxName(): String = throw new UnsupportedOperationException

  override def tensorflowName(): String =
    throw new UnsupportedOperationException

  override def doDiff(f1: java.util.List[SDVariable]): java.util.List[SDVariable] =
    throw new UnsupportedOperationException

//  override def opForDimension(index: Int, dimension: Int): Op = MapOps(x.tensorAlongDimension(index,dimension),f,g)
//
//  override def opForDimension(index: Int, dimension: Int*): Op = MapOps(x.tensorAlongDimension(index,dimension:_*),f,g)

  override def op(origin: Double): Double = f(origin)

  override def op(origin: Float): Float = f(origin).toFloat

  override def op(origin: Short): Short = f(origin).toShort

  override def op(origin: Int): Int = f(origin).toInt

  override def op(origin: Long): Long = f(origin).toLong

  override def op(origin: String): String = ???
}
