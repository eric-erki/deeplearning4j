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

package org.datavec.api.transform.transform.integer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;
import org.nd4j.shade.jackson.annotation.JsonProperty;

/**
 * Replace an empty/missing integer with a certain value.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReplaceEmptyIntegerWithValueTransform extends BaseIntegerTransform {

    private final int value;

    public ReplaceEmptyIntegerWithValueTransform(@JsonProperty("columnName") String columnName,
                    @JsonProperty("value") int value) {
        super(columnName);
        this.value = value;
    }

    @Override
    public Writable map(Writable writable) {
        String s = writable.toString();
        if (s == null || s.isEmpty())
            return new IntWritable(value);
        return writable;
    }

    /**
     * Transform an object
     * in to another object
     *
     * @param input the record to transform
     * @return the transformed writable
     */
    @Override
    public Object map(Object input) {
        return value;
    }
}
