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

package org.datavec.local.transforms;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.transform.TransformProcess;

/**
 * A wrapper around the {@link TransformProcessRecordReader}
 * that uses the {@link LocalTransformExecutor}
 * instead of the {@link TransformProcess} methods.
 *
 * @author Adam Gibson
 */
public class LocalTransformProcessRecordReader extends TransformProcessRecordReader {

    /**
     * Initialize with the internal record reader
     * and the transform process.
     * @param recordReader
     * @param transformProcess
     */
    public LocalTransformProcessRecordReader(RecordReader recordReader, TransformProcess transformProcess) {
        super(recordReader, transformProcess);
    }
}
