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

package org.nd4j.linalg.api.ops.impl.layers.recurrent.weights;

import java.lang.reflect.Array;

import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.common.util.ArrayUtil;

public abstract class RNNWeights {
    public abstract SDVariable[] args();

    public abstract INDArray[] arrayArgs();

    protected static <T> T[] filterNonNull(T... args){
        int count = 0;
        for( int i=0; i<args.length; i++ ) {
            if (args[i] != null) count++;
        }
        T[] out = (T[]) Array.newInstance(args.getClass().getComponentType(), count);
        int j=0;
        for( int i=0; i<args.length; i++ ){
            if(args[i] != null){
                out[j++] = args[i];
            }
        }
        return out;
    }

    public SDVariable[] argsWithInputs(SDVariable... inputs){
        return ArrayUtil.combine(inputs, args());
    }

    public INDArray[] argsWithInputs(INDArray... inputs) {
        return ArrayUtil.combine(inputs, arrayArgs());
    }


}
