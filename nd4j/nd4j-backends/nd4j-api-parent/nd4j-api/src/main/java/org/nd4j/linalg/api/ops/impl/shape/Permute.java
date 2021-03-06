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

package org.nd4j.linalg.api.ops.impl.shape;

import org.apache.commons.lang3.ArrayUtils;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.imports.NoOpNameFoundException;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Collections;
import java.util.List;

/**
 * Permute function
 *
 * @author Adam Gibson
 */
public class Permute extends Transpose {

    private int[] reverseDims;

    public Permute(SameDiff sameDiff, SDVariable i_v, int... permuteDims) {
        super(sameDiff, i_v);
        this.permuteDims = permuteDims;
        this.reverseDims = new int[permuteDims.length];
        for (int i = 0; i < reverseDims.length; i++) {
            reverseDims[i] = ArrayUtils.indexOf(permuteDims, i);
        }
        addIArgument(permuteDims);
    }

    public Permute(INDArray input, INDArray result, int... permuteDims){
        super(input, result);
        this.permuteDims = permuteDims;
        this.reverseDims = new int[permuteDims.length];
        for (int i = 0; i < reverseDims.length; i++) {
            reverseDims[i] = ArrayUtils.indexOf(permuteDims, i);
        }
        addIArgument(permuteDims);
    }

    public Permute(SameDiff sd, SDVariable input, SDVariable permuteDims){
        super(sd, input, permuteDims);
    }

    public Permute(INDArray input, int... permuteDims){
        super(input, null);
        this.permuteDims = permuteDims;
        addIArgument(permuteDims);
    }

    public Permute() {
    }

    @Override
    public String opName() {
        return "permute";
    }

    @Override
    public List<SDVariable> doDiff(List<SDVariable> i_v) {
        SDVariable ret;
        if(args().length == 1) {
            //Static dimensions
            ret = sameDiff.permute(i_v.get(0), reverseDims);
        } else {
            //Dynamic dimensions
            ret = sameDiff.permute(i_v.get(0), sameDiff.invertPermutation(arg(1)));
        }
        return Collections.singletonList(ret);
    }

    @Override
    public String tensorflowName() {
        throw new NoOpNameFoundException("No tensorflow op opName found for " + opName());
    }

    @Override
    public String onnxName() {
        throw new NoOpNameFoundException("No onnx op opName found for " + opName());
    }
}
