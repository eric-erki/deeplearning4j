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

package org.nd4j.linalg.api.ops.impl.scalar;

import lombok.NonNull;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.BaseScalarOp;

import java.util.Collections;
import java.util.List;

/**
 * Scalar addition
 *
 * @author Adam Gibson
 */
public class ScalarAdd extends BaseScalarOp {

    public ScalarAdd() {}

    public ScalarAdd(INDArray x, INDArray y, INDArray z, Number num) {
        super(x, y, z, num);
    }

    public ScalarAdd(INDArray x, Number num) {
        super(x, num);
    }



    public ScalarAdd(INDArray arr) {
        this(arr, 0);
    }

    public ScalarAdd(@NonNull SameDiff sameDiff, @NonNull SDVariable i_v, Number scalar) {
        this(sameDiff, i_v, scalar, false);
    }

    public ScalarAdd(SameDiff sameDiff, SDVariable i_v, Number scalar, boolean inPlace) {
        super(sameDiff, i_v, scalar, inPlace);
    }

    public ScalarAdd(SameDiff sameDiff, SDVariable i_v, Number scalar, boolean inPlace, Object[] extraArgs) {
        super(sameDiff, i_v, scalar, inPlace, extraArgs);
    }

    public ScalarAdd(SameDiff sameDiff, SDVariable i_v, Number scalar, Object[] extraArgs) {
        super(sameDiff, i_v, scalar, extraArgs);
    }

    @Override
    public int opNum() {
        return 0;
    }

    @Override
    public String opName() {
        return "add_scalar";
    }

    @Override
    public List<SDVariable> doDiff(List<SDVariable> i_v1) {
        SDVariable g = i_v1.get(0);
        return Collections.singletonList(g);
    }
}
