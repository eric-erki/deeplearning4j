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

package org.deeplearning4j.nearestneighbor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nd4j.linalg.dataset.DataSet;

import java.io.Serializable;

/**
 * Created by agibsonccc on 12/24/16.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CSVRecord implements Serializable {
    private String[] values;

    /**
     * Instantiate a csv record from a vector
     * given either an input dataset and a
     * one hot matrix, the index will be appended to
     * the end of the record, or for regression
     * it will append all values in the labels
     * @param row the input vectors
     * @return the record from this {@link DataSet}
     */
    public static CSVRecord fromRow(DataSet row) {
        if (!row.getFeatures().isVector() && !row.getFeatures().isScalar())
            throw new IllegalArgumentException("Passed in dataset must represent a scalar or vector");
        if (!row.getLabels().isVector() && !row.getLabels().isScalar())
            throw new IllegalArgumentException("Passed in dataset labels must be a scalar or vector");
        //classification
        CSVRecord record;
        int idx = 0;
        if (row.getLabels().sumNumber().doubleValue() == 1.0) {
            String[] values = new String[row.getFeatures().columns() + 1];
            for (int i = 0; i < row.getFeatures().length(); i++) {
                values[idx++] = String.valueOf(row.getFeatures().getDouble(i));
            }
            int maxIdx = 0;
            for (int i = 0; i < row.getLabels().length(); i++) {
                if (row.getLabels().getDouble(maxIdx) < row.getLabels().getDouble(i)) {
                    maxIdx = i;
                }
            }

            values[idx++] = String.valueOf(maxIdx);
            record = new CSVRecord(values);
        }
        //regression (any number of values)
        else {
            String[] values = new String[row.getFeatures().columns() + row.getLabels().columns()];
            for (int i = 0; i < row.getFeatures().length(); i++) {
                values[idx++] = String.valueOf(row.getFeatures().getDouble(i));
            }
            for (int i = 0; i < row.getLabels().length(); i++) {
                values[idx++] = String.valueOf(row.getLabels().getDouble(i));
            }


            record = new CSVRecord(values);

        }
        return record;
    }

}
