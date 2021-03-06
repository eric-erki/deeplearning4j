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

import lombok.NonNull;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.DynamicCustomOp;

import java.util.Arrays;
import java.util.List;

public class ListDiff extends DynamicCustomOp {

    public ListDiff() {
        //
    }

    public ListDiff(@NonNull SameDiff sd, @NonNull SDVariable x, @NonNull SDVariable y){
        super(sd, new SDVariable[]{x, y});
    }

    public ListDiff(@NonNull INDArray x, @NonNull INDArray y){
        super(new INDArray[]{x, y}, null);
    }

    @Override
    public String tensorflowName() {
        return "ListDiff";  //Note: Seems to be renamed to tf.setdiff1d in public API?
    }

    @Override
    public String opName() {
        return "listdiff";
    }

    @Override
    public List<SDVariable> doDiff(List<SDVariable> f1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<DataType> calculateOutputDataTypes(List<DataType> inputDataTypes){
        //TODO make this configurable
        return Arrays.asList(inputDataTypes.get(0), DataType.INT);
    }
}
