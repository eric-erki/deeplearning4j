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

package org.deeplearning4j.spark.data;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.nd4j.linalg.dataset.DataSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Take an existing DataSet object, and split it into multiple DataSet objects with one example in each
 *
 * Usage:
 * <pre>
 * {@code
 *      RDD<DataSet> myBatchedExampleDataSets = ...;
 *      RDD<DataSet> singleExamlpeDataSets = myBatchedExampleDataSets.mapPartitions(new SplitDataSets(batchSize));
 * }
 * </pre>
 *
 * @author Alex Black
 */
public class SplitDataSetsFunction implements FlatMapFunction<Iterator<DataSet>, DataSet> {
    @Override
    public Iterator<DataSet> call(Iterator<DataSet> dataSetIterator) throws Exception {
        List<DataSet> out = new ArrayList<>();
        while (dataSetIterator.hasNext()) {
            out.addAll(dataSetIterator.next().asList());
        }
        return out.iterator();
    }
}
