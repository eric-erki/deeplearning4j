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

package org.deeplearning4j.arbiter.dropout;

import lombok.AllArgsConstructor;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.FixedValue;
import org.deeplearning4j.nn.conf.dropout.Dropout;
import org.deeplearning4j.nn.conf.dropout.IDropout;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class DropoutSpace implements ParameterSpace<IDropout> {

    private ParameterSpace<Double> dropout;

    public DropoutSpace(double activationRetainProbability){
        this(new FixedValue<>(activationRetainProbability));
    }

    @Override
    public IDropout getValue(double[] parameterValues) {
        return new Dropout(dropout.getValue(parameterValues));
    }

    @Override
    public int numParameters() {
        return dropout.numParameters();
    }

    @Override
    public List<ParameterSpace> collectLeaves() {
        return Collections.<ParameterSpace>singletonList(dropout);
    }

    @Override
    public Map<String, ParameterSpace> getNestedSpaces() {
        return Collections.<String,ParameterSpace>singletonMap("dropout", dropout);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public void setIndices(int... indices) {
        dropout.setIndices(indices);
    }
}
