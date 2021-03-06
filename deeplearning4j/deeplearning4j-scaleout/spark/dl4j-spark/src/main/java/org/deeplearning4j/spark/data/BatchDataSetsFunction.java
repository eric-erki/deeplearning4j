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

import lombok.AllArgsConstructor;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.nd4j.linalg.dataset.DataSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Function used to batch DataSet objects together. Typically used to combine singe-example DataSet objects out of
 * something like {@link org.deeplearning4j.spark.datavec.DataVecDataSetFunction} together into minibatches.<br>
 *
 * Usage:
 * <pre>
 * {@code
 *      RDD<DataSet> mySingleExampleDataSets = ...;
 *      RDD<DataSet> batchData = mySingleExampleDataSets.mapPartitions(new BatchDataSetsFunction(batchSize));
 * }
 * </pre>
 *
 * @author Alex Black
 */
@AllArgsConstructor
public class BatchDataSetsFunction implements FlatMapFunction<Iterator<DataSet>, DataSet> {
    private final int minibatchSize;

    @Override
    public Iterator<DataSet> call(Iterator<DataSet> iter) throws Exception {
        List<DataSet> out = new ArrayList<>();
        while (iter.hasNext()) {
            List<DataSet> list = new ArrayList<>();

            int count = 0;
            while (count < minibatchSize && iter.hasNext()) {
                DataSet ds = iter.next();
                count += ds.getFeatures().size(0);
                list.add(ds);
            }

            DataSet next;
            if (list.isEmpty())
                next = list.get(0);
            else
                next = DataSet.merge(list);

            out.add(next);
        }
        return out.iterator();
    }
}
