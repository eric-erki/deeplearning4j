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

package org.datavec.spark.storage.functions;

import org.apache.hadoop.io.LongWritable;
import org.apache.spark.api.java.function.PairFunction;
import org.datavec.api.writable.Writable;
import org.datavec.hadoop.records.reader.mapfile.record.SequenceRecordWritable;
import scala.Tuple2;

import java.util.List;

/**
 * A simple function to prepare data during loading via {@link org.datavec.spark.storage.SparkStorageUtils}
 *
 * @author Alex Black
 */
public class SequenceRecordLoadPairFunction
                implements PairFunction<Tuple2<LongWritable, SequenceRecordWritable>, Long, List<List<Writable>>> {
    @Override
    public Tuple2<Long, List<List<Writable>>> call(Tuple2<LongWritable, SequenceRecordWritable> t2) throws Exception {
        return new Tuple2<>(t2._1().get(), t2._2().getSequenceRecord());
    }
}
