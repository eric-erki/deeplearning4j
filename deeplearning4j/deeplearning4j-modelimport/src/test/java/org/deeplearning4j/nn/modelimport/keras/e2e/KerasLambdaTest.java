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

package org.deeplearning4j.nn.modelimport.keras.e2e;

import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.samediff.SameDiffLambdaLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.modelimport.keras.KerasLayer;
import org.deeplearning4j.nn.modelimport.keras.KerasModel;
import org.deeplearning4j.nn.modelimport.keras.KerasSequentialModel;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.common.resources.Resources;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


/**
 * Test importing Keras models with multiple Lamdba layers.
 *
 * @author Max Pumperla
 */
public class KerasLambdaTest extends BaseDL4JTest {

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    public class ExponentialLambda extends SameDiffLambdaLayer {
        @Override
        public SDVariable defineLayer(SameDiff sd, SDVariable x) { return x.mul(x); }

        @Override
        public InputType getOutputType(int layerIndex, InputType inputType) { return inputType; }
    }

    public class TimesThreeLambda extends SameDiffLambdaLayer {
        @Override
        public SDVariable defineLayer(SameDiff sd, SDVariable x) { return x.mul(3); }

        @Override
        public InputType getOutputType(int layerIndex, InputType inputType) { return inputType; }
    }


    @Test
    public void testSequentialLambdaLayerImport() throws Exception {
        KerasLayer.registerLambdaLayer("lambda_1", new ExponentialLambda());
        KerasLayer.registerLambdaLayer("lambda_2", new TimesThreeLambda());

        String modelPath = "modelimport/keras/examples/lambda/sequential_lambda.h5";

        try(InputStream is = Resources.asStream(modelPath)) {
            File modelFile = testDir.newFile("tempModel" + System.currentTimeMillis() + ".h5");
            Files.copy(is, modelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            MultiLayerNetwork model = new KerasSequentialModel().modelBuilder().modelHdf5Filename(modelFile.getAbsolutePath())
                    .enforceTrainingConfig(false).buildSequential().getMultiLayerNetwork();

            System.out.println(model.summary());
            INDArray input = Nd4j.create(new int[]{10, 100});

            model.output(input);
        } finally {
            KerasLayer.clearLambdaLayers();
        }
    }

    @Test
    public void testModelLambdaLayerImport() throws Exception {
        KerasLayer.registerLambdaLayer("lambda_3", new ExponentialLambda());
        KerasLayer.registerLambdaLayer("lambda_4", new TimesThreeLambda());

        String modelPath = "modelimport/keras/examples/lambda/model_lambda.h5";

        try(InputStream is = Resources.asStream(modelPath)) {
            File modelFile = testDir.newFile("tempModel" + System.currentTimeMillis() + ".h5");
            Files.copy(is, modelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            ComputationGraph model = new KerasModel().modelBuilder().modelHdf5Filename(modelFile.getAbsolutePath())
                    .enforceTrainingConfig(false).buildModel().getComputationGraph();

            System.out.println(model.summary());
            INDArray input = Nd4j.create(new int[]{10, 784});

            model.output(input);
        } finally {
            KerasLayer.clearLambdaLayers(); // Clear all lambdas, so other tests aren't affected.
        }
    }

}
