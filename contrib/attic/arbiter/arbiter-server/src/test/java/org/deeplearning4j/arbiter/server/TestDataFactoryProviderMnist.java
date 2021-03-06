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

package org.deeplearning4j.arbiter.server;

import lombok.AllArgsConstructor;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIteratorFactory;

@AllArgsConstructor
public class TestDataFactoryProviderMnist extends BaseDL4JTest implements DataSetIteratorFactory {

    private int batchSize;
    private int terminationIter;

    public TestDataFactoryProviderMnist(){
        this(16, 10);
    }

    @Override
    public DataSetIterator create() {
        try {
            return new EarlyTerminationDataSetIterator(new MnistDataSetIterator(batchSize, true, 12345), terminationIter);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
