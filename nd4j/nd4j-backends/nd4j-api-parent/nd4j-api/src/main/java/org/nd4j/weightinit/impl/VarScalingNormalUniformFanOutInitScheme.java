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

package org.nd4j.weightinit.impl;

import lombok.Builder;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.weightinit.BaseWeightInitScheme;
import org.nd4j.weightinit.WeightInit;

/**
 * Initialize the weight to:
 * randn(shape) //N(0, 2/nIn);
 * @author Adam Gibson
 */
public class VarScalingNormalUniformFanOutInitScheme extends BaseWeightInitScheme {

    private double fanOut;

    @Builder
    public VarScalingNormalUniformFanOutInitScheme(char order, double fanOut) {
        super(order);
        this.fanOut = fanOut;
    }

    @Override
    public INDArray doCreate(DataType dataType, long[] shape, INDArray paramsView) {
        double scalingFanOut = 3.0 / Math.sqrt(fanOut);
        return Nd4j.rand(Nd4j.getDistributions().createUniform(-scalingFanOut, scalingFanOut), shape);
    }


    @Override
    public WeightInit type() {
        return WeightInit.VAR_SCALING_UNIFORM_FAN_OUT;
    }
}
