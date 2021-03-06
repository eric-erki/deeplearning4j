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

package org.deeplearning4j.arbiter.util;

import lombok.AllArgsConstructor;
import org.deeplearning4j.arbiter.optimize.api.data.DataProvider;
import org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.Map;

@AllArgsConstructor
public class TestDataProviderMnist implements DataProvider {

    private int batchSize;
    private int terminationIter;

    public TestDataProviderMnist(){
        this(32, 10);
    }

    @Override
    public Object trainData(Map<String, Object> dataParameters) {
        try {
            return new EarlyTerminationDataSetIterator(new MnistDataSetIterator(batchSize, true, 12345), terminationIter);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object testData(Map<String, Object> dataParameters) {
        try {
            return new EarlyTerminationDataSetIterator(new MnistDataSetIterator(batchSize, false, 12345), terminationIter);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> getDataType() {
        return DataSetIterator.class;
    }


}
