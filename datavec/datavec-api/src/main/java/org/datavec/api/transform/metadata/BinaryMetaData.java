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

package org.datavec.api.transform.metadata;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.datavec.api.transform.ColumnType;
import org.datavec.api.writable.Writable;
import org.nd4j.shade.jackson.annotation.JsonProperty;

/**
 * Metadata for an binary column
 *
 * @author Alex Black
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BinaryMetaData extends BaseColumnMetaData {


    public BinaryMetaData(@JsonProperty("name") String name) {
        super(name);
    }



    @Override
    public ColumnType getColumnType() {
        return ColumnType.Bytes;
    }

    @Override
    public boolean isValid(Writable writable) {
        boolean value;
        try {
            value = Boolean.parseBoolean(writable.toString());
        } catch (NumberFormatException e) {
            return false;
        }


        return true;
    }

    /**
     * Is the given object valid for this column,
     * given the column type and any
     * restrictions given by the
     * ColumnMetaData object?
     *
     * @param input object to check
     * @return true if value, false if invalid
     */
    @Override
    public boolean isValid(Object input) {
        boolean value;
        try {
            value = Boolean.parseBoolean(input.toString());
        } catch (NumberFormatException e) {
            return false;
        }


        return true;
    }

    @Override
    public BinaryMetaData clone() {
        return new BinaryMetaData(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BinaryMetaData(name=\"").append(name).append("\",");
        sb.append(")");
        return sb.toString();
    }
}
