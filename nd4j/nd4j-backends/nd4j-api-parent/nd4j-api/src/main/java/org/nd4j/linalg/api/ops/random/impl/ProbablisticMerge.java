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

package org.nd4j.linalg.api.ops.random.impl;

import lombok.NonNull;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.imports.NoOpNameFoundException;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.random.BaseRandomOp;

import java.util.List;

/**
 * @author raver119@gmail.com
 */
public class ProbablisticMerge extends BaseRandomOp {

    private double probability;

    public ProbablisticMerge() {
        super();
    }

    public ProbablisticMerge(@NonNull INDArray x, @NonNull INDArray y, @NonNull INDArray z, double probability) {
        super(x,y,z);
        this.probability = probability;
        this.extraArgs = new Object[] {probability};
    }

    public ProbablisticMerge(@NonNull INDArray x, @NonNull INDArray y, double probability) {
        this(x, y, x, probability);
    }

    @Override
    public int opNum() {
        return 3;
    }

    @Override
    public String opName() {
        return "probablistic_merge";
    }

    @Override
    public String onnxName() {
        throw new NoOpNameFoundException("No onnx op opName found for " +  opName());
    }

    @Override
    public String tensorflowName() {
        throw new NoOpNameFoundException("No tensorflow op opName found for " +  opName());
    }

    @Override
    public List<SDVariable> doDiff(List<SDVariable> f1) {
        return null;
    }
}
