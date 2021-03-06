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

package org.datavec.api.transform.analysis.quality.categorical;

import lombok.AllArgsConstructor;
import org.datavec.api.transform.metadata.CategoricalMetaData;
import org.datavec.api.transform.quality.columns.CategoricalQuality;
import org.datavec.api.writable.NullWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.nd4j.common.function.BiFunction;

import java.io.Serializable;

/**
 * Created by Alex on 5/03/2016.
 */
@AllArgsConstructor
public class CategoricalQualityAddFunction implements BiFunction<CategoricalQuality, Writable, CategoricalQuality>, Serializable {

    private final CategoricalMetaData meta;

    @Override
    public CategoricalQuality apply(CategoricalQuality v1, Writable writable) {

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

        return new CategoricalQuality(valid, invalid, countMissing, countTotal);
    }
}
