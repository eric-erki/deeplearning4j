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

package org.datavec.api.transform.analysis.quality.bytes;

import lombok.Getter;
import org.datavec.api.transform.analysis.quality.QualityAnalysisState;
import org.datavec.api.transform.quality.columns.BytesQuality;
import org.datavec.api.transform.quality.columns.ColumnQuality;
import org.datavec.api.writable.Writable;

/**
 * Created by huitseeker on 3/6/17.
 * NOTE: this class is not ready for production
 * See the {@link BytesQuality} class.

 */
public class BytesQualityAnalysisState implements QualityAnalysisState<BytesQualityAnalysisState> {

    @Getter
    private BytesQuality bytesQuality;

    public BytesQualityAnalysisState() {
        this.bytesQuality = new BytesQuality();
    }

    public BytesQualityAnalysisState add(Writable writable) {
        return this;
    }

    public BytesQualityAnalysisState merge(BytesQualityAnalysisState other) {
        return this;
    }

    public ColumnQuality getColumnQuality() {
        return bytesQuality;
    }
}
