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

package org.nd4j.linalg.api.ops.impl.reduce.bp;

import lombok.NoArgsConstructor;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.linalg.api.ndarray.INDArray;


/**
 * Backprop op for variance reduction operation
 *
 * @author Alex Black
 */
@NoArgsConstructor
public class VarianceBp extends BaseReductionBp {

    private boolean biasCorrected;

    public VarianceBp(SameDiff sameDiff, SDVariable origInput, SDVariable gradAtOutput, boolean biasCorrected, boolean keepDims, int... dimensions) {
        super(sameDiff, origInput, gradAtOutput, keepDims, dimensions);
        this.biasCorrected = biasCorrected;
        addTArgument(biasCorrected ? 1.0 : 0.0);
    }

    public VarianceBp(INDArray origInput, INDArray gradAtOutput, INDArray output, boolean biasCorrected, boolean keepDims, int... dimensions){
        super(origInput, gradAtOutput, output, keepDims, dimensions);
        this.biasCorrected = biasCorrected;
        addTArgument(biasCorrected ? 1.0 : 0.0);
    }

    @Override
    public String opName() {
        return "reduce_variance_bp";
    }
}
