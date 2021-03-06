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

package org.datavec.api.transform.analysis.counter;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.datavec.api.transform.analysis.AnalysisCounter;
import org.datavec.api.writable.Writable;

/**
 * A counter function for doing analysis on BytesWritable columns, on Spark
 *
 * @author Alex Black
 */
@AllArgsConstructor
@Data
public class BytesAnalysisCounter implements AnalysisCounter<BytesAnalysisCounter> {
    private long countTotal = 0;



    public BytesAnalysisCounter() {

    }


    @Override
    public BytesAnalysisCounter add(Writable writable) {
        countTotal++;

        return this;
    }

    public BytesAnalysisCounter merge(BytesAnalysisCounter other) {

        return new BytesAnalysisCounter(countTotal + other.countTotal);
    }

}
