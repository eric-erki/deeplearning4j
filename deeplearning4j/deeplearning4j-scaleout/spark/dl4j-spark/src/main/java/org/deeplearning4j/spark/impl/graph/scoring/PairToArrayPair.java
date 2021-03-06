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

package org.deeplearning4j.spark.impl.graph.scoring;

import org.apache.spark.api.java.function.PairFunction;
import org.nd4j.linalg.api.ndarray.INDArray;
import scala.Tuple2;

/**
 * Simple conversion function for SparkComputationGraph
 *
 * @author Alex Black
 */
public class PairToArrayPair<K> implements PairFunction<Tuple2<K, INDArray>, K, INDArray[]> {
    @Override
    public Tuple2<K, INDArray[]> call(Tuple2<K, INDArray> v1) throws Exception {
        return new Tuple2<>(v1._1(), new INDArray[] {v1._2()});
    }
}
