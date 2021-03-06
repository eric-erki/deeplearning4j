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

package org.datavec.local.transforms.functions;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.StringSplit;
import org.datavec.api.writable.Writable;
import org.nd4j.common.function.Function;

import java.util.List;

/**
 * LineRecordReaderFunction: Used to map a {@code JavaRDD<String>} to a {@code JavaRDD<Collection<Writable>>}
 * Note that this is most useful with LineRecordReader instances (CSVRecordReader, SVMLightRecordReader, etc)
 *
 * @author Alex Black
 */
public class LineRecordReaderFunction implements Function<String, List<Writable>> {
    private final RecordReader recordReader;

    public LineRecordReaderFunction(RecordReader recordReader) {
        this.recordReader = recordReader;
    }

    @Override
    public List<Writable> apply(String s) {
        try {
            recordReader.initialize(new StringSplit(s));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return recordReader.next();
    }
}
