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

package org.nd4j.linalg.api.ops.impl.transforms.segment;

import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.common.base.Preconditions;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.DynamicCustomOp;
import org.nd4j.linalg.api.ops.impl.transforms.segment.bp.UnsortedSegmentMaxBp;

import java.util.*;

/**
 * Unsorted segment max operation
 *
 * @author Alex Black
 */
public class UnsortedSegmentMax extends DynamicCustomOp {

    protected int numSegments;

    public UnsortedSegmentMax(SameDiff sameDiff, SDVariable data, SDVariable segmentIds, int numSegments) {
        super(null, sameDiff,  new SDVariable[] {data, segmentIds}, false);
        this.numSegments = numSegments;
        addIArgument(numSegments);
    }

    public UnsortedSegmentMax(){ }

    public UnsortedSegmentMax(INDArray data, INDArray segmentIds, int numSegments){
        super(new INDArray[]{data, segmentIds}, null);
        this.numSegments = numSegments;
        addIArgument(numSegments);
    }

    @Override
    public String opName(){
        return "unsorted_segment_max";
    }

    @Override
    public String tensorflowName() {
        return "UnsortedSegmentMax";
    }

    @Override
    public List<SDVariable> doDiff(List<SDVariable> gradients){
        return new UnsortedSegmentMaxBp(sameDiff, arg(0), arg(1), gradients.get(0), numSegments).outputs();
    }

    @Override
    public List<DataType> calculateOutputDataTypes(List<DataType> inputDataTypes){
        Preconditions.checkState(inputDataTypes != null && (inputDataTypes.size() == 2 || inputDataTypes.size() == 3),
                "Expected exactly 2 input data types for %s, got %s", getClass(), inputDataTypes);
        return Collections.singletonList(inputDataTypes.get(0));
    }

}
