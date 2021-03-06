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

package org.deeplearning4j.earlystopping.saver;

import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/** Save the best (and latest/most recent) models learned during early stopping training to the local file system.<br>
 * Instances of this class will save 3 files for best (and optionally, latest) models:<br>
 * (a) The network configuration: bestModelConf.json<br>
 * (b) The network parameters: bestModelParams.bin<br>
 * (c) The network updater: bestModelUpdater.bin<br>
 * <br>
 * NOTE: The model updater is an object that contains the internal state for training features such as AdaGrad, Momentum
 * and RMSProp.<br>
 * The updater is <i>not</i> required to use the network at test time; it is saved in case further training is required.
 * Without saving the updater, any further training would result in the updater being recreated, without the benefit
 * of the history/internal state. This could negatively impact training performance after loading the network.
 *
 * @author Alex Black
 */
public class LocalFileModelSaver implements EarlyStoppingModelSaver<MultiLayerNetwork> {

    private static final String BEST_MODEL_BIN = "bestModel.bin";
    private static final String LATEST_MODEL_BIN = "latestModel.bin";
    private String directory;
    private Charset encoding;

    public LocalFileModelSaver(File directory){
        this(directory.getAbsolutePath());
    }

    /**Constructor that uses default character set for configuration (json) encoding
     * @param directory Directory to save networks
     */
    public LocalFileModelSaver(String directory) {
        this(directory, Charset.defaultCharset());
    }

    /**
     * @param directory Directory to save networks
     * @param encoding Character encoding for configuration (json)
     */
    public LocalFileModelSaver(String directory, Charset encoding) {
        this.directory = directory;
        this.encoding = encoding;

        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void saveBestModel(MultiLayerNetwork net, double score) throws IOException {
        String confOut = FilenameUtils.concat(directory, BEST_MODEL_BIN);
        save(net, confOut);
    }

    @Override
    public void saveLatestModel(MultiLayerNetwork net, double score) throws IOException {
        String confOut = FilenameUtils.concat(directory, LATEST_MODEL_BIN);
        save(net, confOut);
    }

    @Override
    public MultiLayerNetwork getBestModel() throws IOException {
        String confOut = FilenameUtils.concat(directory, BEST_MODEL_BIN);
        return load(confOut);
    }

    @Override
    public MultiLayerNetwork getLatestModel() throws IOException {
        String confOut = FilenameUtils.concat(directory, LATEST_MODEL_BIN);
        return load(confOut);
    }

    private void save(MultiLayerNetwork net, String modelName) throws IOException {
        ModelSerializer.writeModel(net, modelName, true);
    }

    private MultiLayerNetwork load(String modelName) throws IOException {
        MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(modelName);
        return net;
    }

    @Override
    public String toString() {
        return "LocalFileModelSaver(dir=" + directory + ")";
    }
}
