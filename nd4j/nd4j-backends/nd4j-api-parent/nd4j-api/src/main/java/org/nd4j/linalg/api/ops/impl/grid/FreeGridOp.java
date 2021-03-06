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

package org.nd4j.linalg.api.ops.impl.grid;

import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.Op;

import java.util.List;

/**
 * Simple GridOp that operates on arbitrary number of Ops, that have no relations between them.
 *
 * @author raver119@gmail.com
 */
public class FreeGridOp extends BaseGridOp {

    public FreeGridOp() {

    }

    public FreeGridOp(INDArray x, INDArray y) {
        super(x, y);
    }

    public FreeGridOp(Op... ops) {
        super(ops);
    }

    public FreeGridOp(List<Op> ops) {
        super(ops);
    }

    @Override
    public int opNum() {
        return 0;
    }

    @Override
    public String opName() {
        return "grid_free";
    }

     @Override
    public List<SDVariable> doDiff(List<SDVariable> f1) {
        return null;
    }
}
