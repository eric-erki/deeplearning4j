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

package org.deeplearning4j.nn.conf.layers;

import lombok.NoArgsConstructor;
import org.deeplearning4j.nn.api.ParamInitializer;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.params.EmptyParamInitializer;
import org.nd4j.linalg.learning.regularization.Regularization;

import java.util.List;

@NoArgsConstructor
public abstract class NoParamLayer extends Layer {

    protected NoParamLayer(Builder builder) {
        super(builder);
    }

    @Override
    public ParamInitializer initializer() {
        return EmptyParamInitializer.getInstance();
    }

    @Override
    public void setNIn(InputType inputType, boolean override) {
        //No op in most no param layers
    }

    @Override
    public List<Regularization> getRegularizationByParam(String paramName){
        //No parameters -> no regularization of parameters
        return null;
    }

    @Override
    public GradientNormalization getGradientNormalization() {
        return GradientNormalization.None;
    }

    @Override
    public double getGradientNormalizationThreshold() {
        return 0;
    }

    @Override
    public boolean isPretrainParam(String paramName) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not contain parameters");
    }
}
