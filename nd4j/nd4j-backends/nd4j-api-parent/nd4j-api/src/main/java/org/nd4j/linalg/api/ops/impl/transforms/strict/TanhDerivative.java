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

import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.imports.NoOpNameFoundException;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.BaseTransformStrictOp;

import java.util.List;

/**
 * Tanh derivative
 *
 * @deprecated Use {@link org.nd4j.linalg.api.ops.impl.transforms.gradient.TanhDerivative}.
 */
@Deprecated
public class TanhDerivative extends BaseTransformStrictOp {
    public TanhDerivative(SameDiff sameDiff, SDVariable i_v, boolean inPlace) {
        super(sameDiff, i_v, inPlace);
    }

    public TanhDerivative() {}

    public TanhDerivative(INDArray x, INDArray z) {
        super(x, z);
    }

    public TanhDerivative(INDArray x) {
        super(x);
    }

    @Override
    public int opNum() {
        return 4;
    }

    @Override
    public String opName() {
        return "_tanhderivative";
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
