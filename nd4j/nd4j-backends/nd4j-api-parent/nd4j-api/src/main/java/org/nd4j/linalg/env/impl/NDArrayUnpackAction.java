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

package org.nd4j.linalg.env.impl;

import lombok.val;
import org.nd4j.linalg.env.EnvironmentalAction;
import org.nd4j.linalg.factory.Nd4j;

public class NDArrayUnpackAction implements EnvironmentalAction {
    @Override
    public String targetVariable() {
        return "ND4J_PREVENT_UNPACK";
    }

    @Override
    public void process(String value) {
        val v = Boolean.valueOf(value).booleanValue();


        // FIXME: get rid of direct variable access eventually!!1
        Nd4j.preventUnpack = v;
    }
}
