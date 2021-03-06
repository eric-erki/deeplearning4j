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

package org.datavec.api.io.converters;

import org.datavec.api.io.WritableConverter;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;

import java.util.List;

/**
 * Convert a label in to an index based on the
 *
 *
 * @author Adam Gibson
 */
public class LabelWriterConverter implements WritableConverter {
    private List<String> labels;

    public LabelWriterConverter(List<String> labels) {
        this.labels = labels;
    }

    @Override
    public Writable convert(Writable writable) throws WritableConverterException {
        String label = writable.toString();
        return new IntWritable(labels.indexOf(label));
    }
}
