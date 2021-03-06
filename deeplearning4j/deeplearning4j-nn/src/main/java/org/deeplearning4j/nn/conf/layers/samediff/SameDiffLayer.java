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

package org.deeplearning4j.nn.conf.layers.samediff;

import lombok.*;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.MaskState;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.weights.IWeightInit;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.common.primitives.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A base layer used for implementing Deeplearning4j layers using SameDiff. These layers are not scoring/output layers:
 * that is, they should be used as the intermediate layer in a network only.<br> To implement an output layer, extend
 * {@link SameDiffOutputLayer} instead.<br> Note also that if multiple inputs are required, it is possible to implement
 * a vertex instead: {@link SameDiffVertex}<br>
 * <br>
 * To implement a Deeplearning layer using SameDiff, extend this class.<br> There are 4 required methods:<br> -
 * defineLayer: Defines the forward pass for the layer<br> - defineParameters: Define the layer's parameters in a way
 * suitable for DL4J<br> - initializeParameters: if required, set the initial parameter values for the layer<br> -
 * getOutputType: determine the type of output/activations for the layer (without actually executing the layer's forward
 * pass)<br>
 * <br>
 * Furthermore, there are 3 optional methods:<br> - setNIn(InputType inputType, boolean override): if implemented, set
 * the number of inputs to the layer during network initialization<br> - getPreProcessorForInputType: return the
 * preprocessor that should be added (if any), for the given input type<br> - applyGlobalConfigToLayer: apply any global
 * configuration options (weight init, activation functions etc) to the layer's configuration.<br>
 *
 * @author Alex Black
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SameDiffLayer extends AbstractSameDiffLayer {

    protected WeightInit weightInit;
    protected Map<String,IWeightInit> paramWeightInit;

    protected SameDiffLayer(Builder builder) {
        super(builder);
        this.weightInit = builder.weightInit;
        this.paramWeightInit = builder.paramWeightInit;
    }

    protected SameDiffLayer() {
        //No op constructor for Jackson
    }

    /**
     * Define the layer
     *
     * @param sameDiff SameDiff instance
     * @param layerInput Input to the layer
     * @param paramTable Parameter table - keys as defined by {@link #defineParameters(SDLayerParams)}
     * @param mask Optional, maybe null. Mask to apply if supported
     * @return The final layer variable corresponding to the activations/output from the forward pass
     */
    public abstract SDVariable defineLayer(SameDiff sameDiff, SDVariable layerInput,
                                           Map<String, SDVariable> paramTable, SDVariable mask);

    /**
     * @see Layer#feedForwardMaskArray(INDArray, MaskState, int)
     */
    public Pair<INDArray, MaskState> feedForwardMaskArray(org.nd4j.linalg.api.ndarray.INDArray maskArray, org.deeplearning4j.nn.api.MaskState currentMaskState, int minibatchSize){
        return new Pair<>(maskArray, currentMaskState);
    }

    /**
     * Validate input arrays to confirm that they fulfill the assumptions of the layer. If they don't, throw an exception.
     * @param input input to the layer
     */
    public void validateInput(INDArray input){/* no-op */}

    //==================================================================================================================

    @Override
    public org.deeplearning4j.nn.api.Layer instantiate(NeuralNetConfiguration conf,
                                                       Collection<TrainingListener> trainingListeners, int layerIndex, INDArray layerParamsView,
                                                       boolean initializeParams, DataType networkDataType) {
        org.deeplearning4j.nn.layers.samediff.SameDiffLayer ret =
                        new org.deeplearning4j.nn.layers.samediff.SameDiffLayer(conf, networkDataType);
        ret.setIndex(layerIndex);
        ret.setParamsViewArray(layerParamsView);
        Map<String, INDArray> paramTable = initializer().init(conf, layerParamsView, initializeParams);
        ret.setParamTable(paramTable);
        ret.setConf(conf);
        return ret;
    }

    @SuppressWarnings("unchecked")
    @Getter
    @Setter
    public static abstract class Builder<T extends Builder<T>> extends AbstractSameDiffLayer.Builder<T> {

        protected WeightInit weightInit = WeightInit.XAVIER;
        protected Map<String,IWeightInit> paramWeightInit;

        /**
         * @param weightInit Weight initialization to use for the layer
         */
        public T weightInit(WeightInit weightInit) {
            this.setWeightInit(weightInit);
            return (T) this;
        }

        public T weightInit(@NonNull String param, @NonNull IWeightInit weightInit){
            if(paramWeightInit == null)
                paramWeightInit = new HashMap<>();
            paramWeightInit.put(param, weightInit);
            return (T) this;
        }
    }
}
