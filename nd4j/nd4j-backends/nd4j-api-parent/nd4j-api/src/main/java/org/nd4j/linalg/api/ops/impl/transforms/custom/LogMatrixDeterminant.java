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

package org.nd4j.linalg.api.ops.impl.transforms.custom;

import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.common.base.Preconditions;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ops.DynamicCustomOp;

import java.util.Collections;
import java.util.List;

/**
 * Log Matrix Determinant op
 *
 * Given input with shape [..., N, N] output the log determinant for each sub-matrix.
 *
 * @author Alex Black
 */
public class LogMatrixDeterminant extends DynamicCustomOp {

    public LogMatrixDeterminant() {
        //
    }

    public LogMatrixDeterminant(SameDiff sameDiff, SDVariable in, boolean inPlace) {
        super(null, sameDiff, new SDVariable[]{in}, inPlace);
    }


    @Override
    public String opName() {
        return "log_matrix_determinant";
    }

    @Override
    public String tensorflowName() {
        return "LogMatrixDeterminant";
    }

    @Override
    public List<SDVariable> doDiff(List<SDVariable> i_v) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<DataType> calculateOutputDataTypes(List<DataType> dataTypes){
        Preconditions.checkState(dataTypes != null && dataTypes.size() == 1, "Expected exactly 1 input datatype for %s, got %s", getClass(), dataTypes);
        Preconditions.checkState(dataTypes.get(0).isFPType(), "Input datatype must be a floating point type, got %s", dataTypes.get(0));
        return Collections.singletonList(dataTypes.get(0));
    }
}
