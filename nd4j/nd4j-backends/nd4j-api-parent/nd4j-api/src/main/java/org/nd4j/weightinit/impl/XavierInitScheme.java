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
import org.apache.commons.math3.util.FastMath;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.weightinit.BaseWeightInitScheme;
import org.nd4j.weightinit.WeightInit;

/**
 * Initialize the weight to:
 * range = = U * sqrt(2.0 / (fanIn + fanOut)
 * @author Adam Gibson
 */
public class XavierInitScheme extends BaseWeightInitScheme {

    private double fanIn;
    private double fanOut;

    @Builder
    public XavierInitScheme(char order, double fanIn, double fanOut) {
        super(order);
        this.fanIn = fanIn;
        this.fanOut = fanOut;
    }


    @Override
    public INDArray doCreate(DataType dataType, long[] shape, INDArray paramsView) {
        return Nd4j.randn(dataType, order(), shape).muli(FastMath.sqrt(2.0 / (fanIn + fanOut)));
    }


    @Override
    public WeightInit type() {
        return WeightInit.XAVIER;
    }
}
