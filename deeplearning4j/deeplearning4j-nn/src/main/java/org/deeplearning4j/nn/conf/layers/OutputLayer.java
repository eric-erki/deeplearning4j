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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.ParamInitializer;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.params.DefaultParamInitializer;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.nd4j.linalg.activations.impl.ActivationSoftmax;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import java.util.Collection;
import java.util.Map;

/**
 * Output layer used for training via backpropagation based on labels and a specified loss function. Can be configured
 * for both classification and regression. Note that OutputLayer has parameters - it contains a fully-connected layer
 * (effectively contains a DenseLayer) internally. This allows the output size to be different to the layer input size.
 * OutputLayer is equivalent to ({@link DenseLayer} + {@link LossLayer})
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OutputLayer extends BaseOutputLayer {

    protected OutputLayer(Builder builder) {
        super(builder);
        initializeConstraints(builder);
    }

    @Override
    public Layer instantiate(NeuralNetConfiguration conf, Collection<TrainingListener> trainingListeners,
                             int layerIndex, INDArray layerParamsView, boolean initializeParams, DataType networkDataType) {
        LayerValidation.assertNInNOutSet("OutputLayer", getLayerName(), layerIndex, getNIn(), getNOut());

        org.deeplearning4j.nn.layers.OutputLayer ret = new org.deeplearning4j.nn.layers.OutputLayer(conf, networkDataType);
        ret.setListeners(trainingListeners);
        ret.setIndex(layerIndex);
        ret.setParamsViewArray(layerParamsView);
        Map<String, INDArray> paramTable = initializer().init(conf, layerParamsView, initializeParams);
        ret.setParamTable(paramTable);
        ret.setConf(conf);
        return ret;
    }

    @Override
    public ParamInitializer initializer() {
        return DefaultParamInitializer.getInstance();
    }

    public static class Builder extends BaseOutputLayer.Builder<Builder> {

        public Builder() {
            //Set default activation function to softmax (to match default loss function MCXENT)
            this.setActivationFn(new ActivationSoftmax());
        }

        /**
         * @param lossFunction Loss function for the output layer
         */
        public Builder(LossFunction lossFunction) {
            super.lossFunction(lossFunction);
            //Set default activation function to softmax (for consistent behaviour with no-arg constructor)
            this.setActivationFn(new ActivationSoftmax());
        }

        /**
         * @param lossFunction Loss function for the output layer
         */
        public Builder(ILossFunction lossFunction) {
            this.setLossFn(lossFunction);
            //Set default activation function to softmax (for consistent behaviour with no-arg constructor)
            this.setActivationFn(new ActivationSoftmax());
        }

        @Override
        @SuppressWarnings("unchecked")
        public OutputLayer build() {
            return new OutputLayer(this);
        }
    }
}

