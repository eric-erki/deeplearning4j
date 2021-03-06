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

package org.deeplearning4j.earlystopping.trainer;

import org.deeplearning4j.datasets.iterator.MultiDataSetWrapperIterator;
import org.deeplearning4j.datasets.iterator.impl.SingletonDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.SingletonMultiDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.listener.EarlyStoppingListener;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;

/**
 * Class for conducting early stopping training locally (single machine).<br>
 * Can be used to train a {@link ComputationGraph}
 */
public class EarlyStoppingGraphTrainer extends BaseEarlyStoppingTrainer<ComputationGraph> { //implements IEarlyStoppingTrainer<ComputationGraph> {
    private ComputationGraph net;

    /**
     * @param esConfig Configuration
     * @param net Network to train using early stopping
     * @param train DataSetIterator for training the network
     */
    public EarlyStoppingGraphTrainer(EarlyStoppingConfiguration<ComputationGraph> esConfig, ComputationGraph net,
                    DataSetIterator train) {
        this(esConfig, net, train, null);
    }

    /**Constructor for training using a {@link DataSetIterator}
     * @param esConfig Configuration
     * @param net Network to train using early stopping
     * @param train DataSetIterator for training the network
     * @param listener Early stopping listener. May be null.
     */
    public EarlyStoppingGraphTrainer(EarlyStoppingConfiguration<ComputationGraph> esConfig, ComputationGraph net,
                    DataSetIterator train, EarlyStoppingListener<ComputationGraph> listener) {
        super(esConfig, net, train, null, listener);
        if (net.getNumInputArrays() != 1 || net.getNumOutputArrays() != 1)
            throw new IllegalStateException(
                            "Cannot do early stopping training on ComputationGraph with DataSetIterator: graph does not have 1 input and 1 output array");
        this.net = net;
    }

    /**Constructor for training using a {@link MultiDataSetIterator}
     * @param esConfig Configuration
     * @param net Network to train using early stopping
     * @param train DataSetIterator for training the network
     * @param listener Early stopping listener. May be null.
     */
    public EarlyStoppingGraphTrainer(EarlyStoppingConfiguration<ComputationGraph> esConfig, ComputationGraph net,
                    MultiDataSetIterator train, EarlyStoppingListener<ComputationGraph> listener) {
        super(esConfig, net, null, train, listener);
        this.net = net;
    }

    @Override
    protected void fit(DataSet ds) {
        net.fit(ds);
    }

    @Override
    protected void fit(MultiDataSet mds) {
        net.fit(mds);
    }

    @Override
    protected void pretrain(DataSet ds) {
        net.pretrain(new SingletonDataSetIterator(ds));
    }

    @Override
    protected void pretrain(MultiDataSet mds) {
        net.pretrain(new SingletonMultiDataSetIterator(mds));
    }
}
