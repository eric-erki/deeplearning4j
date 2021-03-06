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

package org.nd4j.imports.descriptors.properties.adapters;

import lombok.AllArgsConstructor;
import org.nd4j.autodiff.functions.DifferentialFunction;
import org.nd4j.imports.descriptors.properties.AttributeAdapter;

import java.lang.reflect.Field;

@AllArgsConstructor
public class ConditionalFieldValueIntIndexArrayAdapter implements AttributeAdapter {
    private Object targetValue;
    private int trueIndex,falseIndex;
    private Field fieldName;

    @Override
    public void mapAttributeFor(Object inputAttributeValue, Field fieldFor, DifferentialFunction on) {
        int[] inputValue = (int[]) inputAttributeValue;
        Object comp = on.getValue(fieldName);
        if(targetValue.equals(comp)) {
            on.setValueFor(fieldFor,inputValue[trueIndex]);
        } else {
            on.setValueFor(fieldFor,inputValue[falseIndex]);
        }
    }
}
