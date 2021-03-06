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
import org.nd4j.imports.NoOpNameFoundException;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.BaseTransformStrictOp;

import java.util.Arrays;
import java.util.List;

/**
 * ACosh elementwise function
 *
 * @author Adam Gibson
 */
@NoArgsConstructor
public class ACosh extends BaseTransformStrictOp {

    public ACosh(INDArray x) {
        super(x);
    }

    public ACosh(INDArray x, INDArray y) {
        super(x, y);
    }

    public ACosh(SameDiff sameDiff, SDVariable i_v) {
        this(sameDiff, i_v, false);
    }

    public ACosh(SameDiff sameDiff, SDVariable i_v, boolean inPlace) {
        super(sameDiff, i_v, inPlace);
    }

    @Override
    public int opNum() {
        return 46;
    }

    @Override
    public String opName() {
        return "acosh";
    }

    @Override
    public String onnxName() {
        throw new NoOpNameFoundException("No onnx op opName found for " + opName());
    }

    @Override
    public String tensorflowName() {
        return "Acosh";
    }


    @Override
    public List<SDVariable> doDiff(List<SDVariable> i_v) {
        //dacosh(x)/dx = 1/(sqrt(x^2-1)) -- note that domain is x >= 1
        SDVariable xSqPlus1 = sameDiff.math().square(arg()).sub(1.0);
        SDVariable sqrt = sameDiff.math().sqrt(xSqPlus1);
        return Arrays.asList(i_v.get(0).div(sqrt));
    }

}
