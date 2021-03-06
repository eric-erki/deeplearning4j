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

package org.nd4j.linalg.api.ops.impl.transforms.strict;

import lombok.NoArgsConstructor;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.BaseTransformStrictOp;
import org.nd4j.linalg.api.ops.impl.transforms.gradient.HardSigmoidBp;

import java.util.List;

/**
 * HardSigmoid function
 *
 * @author raver119@gmail.com
 */
@NoArgsConstructor
public class HardSigmoid extends BaseTransformStrictOp {

    public HardSigmoid(INDArray x, INDArray z) {
        super(x, z);
    }

    public HardSigmoid(INDArray ndArray) {
        super(ndArray);
    }

    public HardSigmoid(SameDiff sameDiff, SDVariable in, boolean inPlace){
        super(sameDiff, in, inPlace);
    }

    public HardSigmoid(SameDiff sameDiff, SDVariable in){
        this(sameDiff, in, false);
    }

    @Override
    public int opNum() {
        return 36;
    }

    @Override
    public String opName() {
        return "hard_sigmoid";
    }

    @Override
    public String onnxName() {
        return "HardSigmoid";
    }

    @Override
    public String tensorflowName() {
        return "HardSigmoid";
    }

    @Override
    public List<SDVariable> doDiff(List<SDVariable> f1) {
        return new HardSigmoidBp(sameDiff, arg(), f1.get(0)).outputs();
    }


}
