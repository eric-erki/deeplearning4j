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

package org.datavec.api.transform.analysis.quality.time;

import lombok.AllArgsConstructor;
import org.datavec.api.transform.metadata.TimeMetaData;
import org.datavec.api.transform.quality.columns.TimeQuality;
import org.datavec.api.writable.NullWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.nd4j.common.function.BiFunction;

import java.io.Serializable;

@AllArgsConstructor
public class TimeQualityAddFunction implements BiFunction<TimeQuality, Writable, TimeQuality>, Serializable {

    private final TimeMetaData meta;

    @Override
    public TimeQuality apply(TimeQuality v1, Writable writable) {

        long valid = v1.getCountValid();
        long invalid = v1.getCountInvalid();
        long countMissing = v1.getCountMissing();
        long countTotal = v1.getCountTotal() + 1;

        if (meta.isValid(writable))
            valid++;
        else if (writable instanceof NullWritable
                        || writable instanceof Text && (writable.toString() == null || writable.toString().isEmpty()))
            countMissing++;
        else
            invalid++;

        return new TimeQuality(valid, invalid, countMissing, countTotal);
    }
}
