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

package org.datavec.spark.inference.model.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kepricon on 17. 5. 24.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchImageRecord {
    private List<SingleImageRecord> records;

    /**
     * Add a record
     * @param record
     */
    public void add(SingleImageRecord record) {
        if (records == null)
            records = new ArrayList<>();
        records.add(record);
    }

    public void add(URI uri) {
        this.add(new SingleImageRecord(uri));
    }
}
