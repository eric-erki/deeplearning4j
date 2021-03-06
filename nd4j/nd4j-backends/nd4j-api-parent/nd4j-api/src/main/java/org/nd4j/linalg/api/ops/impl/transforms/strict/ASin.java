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

import java.util.Collections;
import java.util.List;

/**
 * Arcsin elementwise function
 *
 * @author Adam Gibson
 */
@NoArgsConstructor
public class ASin extends BaseTransformStrictOp {
    public ASin(SameDiff sameDiff, SDVariable i_v, boolean inPlace) {
        super(sameDiff, i_v, inPlace);
    }

    public ASin(SameDiff sameDiff, SDVariable i_v) {

        this(sameDiff, i_v, false);
    }

    public ASin(INDArray x, INDArray z) {
        super(x, z);
    }

    public ASin(INDArray x) {
        super(x);
    }

    @Override
    public int opNum() {
        return 31;
    }

    @Override
    public String opName() {
        return "asin";
    }

    @Override
    public String onnxName() {
        throw new NoOpNameFoundException("No onnx op opName found for " + opName());
    }

    @Override
    public String tensorflowName() {
        return "Asin";
    }


    @Override
    public List<SDVariable> doDiff(List<SDVariable> i_v) {
        //d(asin(x))/dx = 1/sqrt(1-x^2)
        SDVariable oneSubSq = sameDiff.math().square(arg()).rsub(1.0);
        SDVariable ret = sameDiff.math().sqrt(oneSubSq).rdiv(1.0).mul(i_v.get(0));
        return Collections.singletonList(ret);
    }


}
