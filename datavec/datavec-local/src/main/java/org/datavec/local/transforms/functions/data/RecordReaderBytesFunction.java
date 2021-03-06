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

package org.datavec.local.transforms.functions.data;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.writable.BytesWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.nd4j.common.function.Function;
import org.nd4j.common.primitives.Pair;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**RecordReaderBytesFunction: Converts binary data (in the form of a BytesWritable) to DataVec format data
 * ({@code Collection<Writable>}) using a RecordReader
 * @author Alex Black
 */
public class RecordReaderBytesFunction implements Function<Pair<Text, BytesWritable>, List<Writable>> {

    private final RecordReader recordReader;

    public RecordReaderBytesFunction(RecordReader recordReader) {
        this.recordReader = recordReader;
    }

    @Override
    public List<Writable> apply(Pair<Text, BytesWritable> v1) {
        URI uri = URI.create(v1.getRight().toString());
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(v1.getRight().getContent()));
        try {
            return recordReader.record(uri, dis);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }


}
