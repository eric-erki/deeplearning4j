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

package org.nd4j.linalg.api.ops.impl.transforms.pairwise.arithmetic;

import lombok.NonNull;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.transforms.BaseDynamicTransformOp;
import org.nd4j.linalg.api.ops.impl.transforms.pairwise.arithmetic.bp.FloorDivBpOp;

import java.util.List;

/**
 * Truncated division operation
 *
 * @author Adam Gibson
 */
public class FloorDivOp extends BaseDynamicTransformOp {
    public FloorDivOp() {}

    public FloorDivOp(SameDiff sameDiff, SDVariable x, SDVariable y){
        this(sameDiff, new SDVariable[]{x,y}, false);
    }

    public FloorDivOp( SameDiff sameDiff, SDVariable[] args, boolean inPlace) {
        super(sameDiff, args, inPlace);
    }

    public FloorDivOp(@NonNull INDArray x, @NonNull INDArray y) {
        this(new INDArray[]{x, y}, null);
    }

    public FloorDivOp( INDArray[] inputs, INDArray[] outputs) {
        super(inputs, outputs);
    }


    @Override
    public String opName() {
        return "floordiv";
    }

    @Override
    public String onnxName() {
        return "FloorDiv";
    }

    @Override
    public String tensorflowName() {
        return "FloorDiv";
    }



    @Override
    public List<SDVariable> doDiff(List<SDVariable> i_v) {
        return new FloorDivBpOp(sameDiff, larg(), rarg(), i_v.get(0)).outputs();
    }
}
