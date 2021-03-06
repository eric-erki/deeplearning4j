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

package org.deeplearning4j.arbiter.optimize;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.arbiter.ComputationGraphSpace;
import org.deeplearning4j.arbiter.MultiLayerSpace;
import org.deeplearning4j.arbiter.conf.updater.SgdSpace;
import org.deeplearning4j.arbiter.layers.ConvolutionLayerSpace;
import org.deeplearning4j.arbiter.layers.DenseLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.api.data.DataProvider;
import org.deeplearning4j.arbiter.optimize.api.data.DataSource;
import org.deeplearning4j.arbiter.optimize.api.score.ScoreFunction;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxCandidatesCondition;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxTimeCondition;
import org.deeplearning4j.arbiter.optimize.api.termination.TerminationCondition;
import org.deeplearning4j.arbiter.optimize.config.OptimizationConfiguration;
import org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator;
import org.deeplearning4j.arbiter.optimize.parameter.continuous.ContinuousParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.integer.IntegerParameterSpace;
import org.deeplearning4j.arbiter.optimize.runner.IOptimizationRunner;
import org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner;
import org.deeplearning4j.arbiter.optimize.runner.listener.StatusListener;
import org.deeplearning4j.arbiter.saver.local.FileModelSaver;
import org.deeplearning4j.arbiter.scoring.impl.EvaluationScoreFunction;
import org.deeplearning4j.arbiter.scoring.impl.TestSetLossScoreFunction;
import org.deeplearning4j.arbiter.task.ComputationGraphTaskCreator;
import org.deeplearning4j.arbiter.task.MultiLayerNetworkTaskCreator;
import org.deeplearning4j.arbiter.ui.listener.ArbiterStatusListener;
import org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.model.storage.InMemoryStatsStorage;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.common.function.Function;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alex on 19/07/2017.
 */
@Slf4j
public class TestBasic extends BaseDL4JTest {

    @Override
    public long getTimeoutMilliseconds() {
        return 3600_000L;
    }

    @Test
    @Ignore
    public void testBasicUiOnly() throws Exception {

        UIServer.getInstance();

        Thread.sleep(1000_000);
    }

    @Test
    @Ignore
    public void testBasicMnist() throws Exception {
        Nd4j.setDefaultDataTypes(DataType.FLOAT, DataType.FLOAT);

        MultiLayerSpace mls = getMultiLayerSpaceMnist();
        Map<String, Object> commands = new HashMap<>();
//        commands.put(DataSetIteratorFactoryProvider.FACTORY_KEY, TestDataFactoryProviderMnist.class.getCanonicalName());

        //Define configuration:
        CandidateGenerator candidateGenerator = new RandomSearchGenerator(mls, commands);
        DataProvider dataProvider = new MnistDataSetProvider();


        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterUiTestBasicMnist\\").getAbsolutePath();

        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();
        f.mkdir();
        if (!f.exists())
            throw new RuntimeException();

        OptimizationConfiguration configuration =
                new OptimizationConfiguration.Builder()
                        .candidateGenerator(candidateGenerator).dataProvider(dataProvider)
                        .modelSaver(new FileModelSaver(modelSavePath))
                        .scoreFunction(new TestSetLossScoreFunction(true))
                        .terminationConditions(new MaxTimeCondition(120, TimeUnit.MINUTES),
                                new MaxCandidatesCondition(100))
                        .build();

        IOptimizationRunner runner =
                new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());

        StatsStorage ss = new InMemoryStatsStorage();
        StatusListener sl = new ArbiterStatusListener(ss);
        runner.addListeners(sl);

        UIServer.getInstance().attach(ss);

        runner.execute();
        Thread.sleep(1000_000);
    }

    private static MultiLayerSpace getMultiLayerSpaceMnist() {
        return new MultiLayerSpace.Builder()
                .updater(new SgdSpace(new ContinuousParameterSpace(0.0001, 0.2)))
                .l2(new ContinuousParameterSpace(0.0001, 0.05))
                .addLayer(
                        new ConvolutionLayerSpace.Builder().nIn(1)
                                .nOut(new IntegerParameterSpace(5, 30))
                                .kernelSize(new DiscreteParameterSpace<>(new int[]{3, 3},
                                        new int[]{4, 4}, new int[]{5, 5}))
                                .stride(new DiscreteParameterSpace<>(new int[]{1, 1},
                                        new int[]{2, 2}))
                                .activation(new DiscreteParameterSpace<>(Activation.RELU,
                                        Activation.SOFTPLUS, Activation.LEAKYRELU))
                                .build())
                .addLayer(new DenseLayerSpace.Builder().nOut(new IntegerParameterSpace(32, 128))
                        .activation(new DiscreteParameterSpace<>(Activation.RELU, Activation.TANH))
                        .build(), new IntegerParameterSpace(0, 1), true) //0 to 1 layers
                .addLayer(new OutputLayerSpace.Builder().nOut(10).activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT).build())
                .setInputType(InputType.convolutionalFlat(28, 28, 1))
                .build();
    }

    @Test
    @Ignore
    public void testBasicMnistDataSource() throws InterruptedException {
        ParameterSpace<Double> learningRateHyperparam = new ContinuousParameterSpace(0.0001, 0.1);
        ParameterSpace<Integer> layerSizeHyperparam = new IntegerParameterSpace(16, 256);

        MultiLayerSpace hyperparameterSpace = new MultiLayerSpace.Builder()
                .weightInit(WeightInit.XAVIER)
                .l2(0.0001)
                .updater(new SgdSpace(learningRateHyperparam))
                .addLayer(new DenseLayerSpace.Builder()
                        .nIn(784)
                        .activation(Activation.LEAKYRELU)
                        .nOut(layerSizeHyperparam)
                        .build())
                .addLayer(new OutputLayerSpace.Builder()
                        .nOut(10)
                        .activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT)
                        .build())
                .build();
        CandidateGenerator candidateGenerator = new RandomSearchGenerator(hyperparameterSpace, null);
        ScoreFunction scoreFunction = new EvaluationScoreFunction(Evaluation.Metric.ACCURACY);
        TerminationCondition[] terminationConditions = {
                new MaxTimeCondition(5, TimeUnit.MINUTES),
                new MaxCandidatesCondition(2)};

        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterUiTestBasicMnist\\").getAbsolutePath();

        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();
        f.mkdir();
        if (!f.exists())
            throw new RuntimeException();
        Class<? extends DataSource> ds = MnistDataSource.class;
        Properties dsp = new Properties();
        dsp.setProperty("minibatch", "8");
        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder()
                .candidateGenerator(candidateGenerator).dataSource(ds, dsp)
                .modelSaver(new FileModelSaver(modelSavePath))
                .scoreFunction(scoreFunction)
                .terminationConditions(terminationConditions)
                .build();

        IOptimizationRunner runner = new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());

        StatsStorage ss = new InMemoryStatsStorage();
        StatusListener sl = new ArbiterStatusListener(ss);
        runner.addListeners(sl);

        UIServer.getInstance().attach(ss);

        runner.execute();
        Thread.sleep(90000);
    }


    @Test
    @Ignore
    public void testBasicMnistCompGraph() throws Exception {

        ComputationGraphSpace cgs = new ComputationGraphSpace.Builder()
                .updater(new SgdSpace(new ContinuousParameterSpace(0.0001, 0.2)))
                .l2(new ContinuousParameterSpace(0.0001, 0.05))
                .addInputs("in")
                .addLayer("0",
                        new ConvolutionLayerSpace.Builder().nIn(1)
                                .nOut(new IntegerParameterSpace(5, 30))
                                .kernelSize(new DiscreteParameterSpace<>(new int[]{3, 3},
                                        new int[]{4, 4}, new int[]{5, 5}))
                                .stride(new DiscreteParameterSpace<>(new int[]{1, 1},
                                        new int[]{2, 2}))
                                .activation(new DiscreteParameterSpace<>(Activation.RELU,
                                        Activation.SOFTPLUS, Activation.LEAKYRELU))
                                .build(), "in")
                .addLayer("1", new DenseLayerSpace.Builder().nOut(new IntegerParameterSpace(32, 128))
                        .activation(new DiscreteParameterSpace<>(Activation.RELU, Activation.TANH))
                        .build(), "0")
                .addLayer("out", new OutputLayerSpace.Builder().nOut(10).activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT).build(), "1")
                .setOutputs("out")
                .setInputTypes(InputType.convolutionalFlat(28, 28, 1))
                .build();

        //Define configuration:
        CandidateGenerator candidateGenerator = new RandomSearchGenerator(cgs);
        DataProvider dataProvider = new MnistDataSetProvider();


        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterUiTestBasicMnistCG\\").getAbsolutePath();

        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();
        f.mkdir();
        if (!f.exists())
            throw new RuntimeException();

        OptimizationConfiguration configuration =
                new OptimizationConfiguration.Builder()
                        .candidateGenerator(candidateGenerator).dataProvider(dataProvider)
                        .modelSaver(new FileModelSaver(modelSavePath))
                        .scoreFunction(new TestSetLossScoreFunction(true))
                        .terminationConditions(new MaxTimeCondition(120, TimeUnit.MINUTES),
                                new MaxCandidatesCondition(100))
                        .build();

        IOptimizationRunner runner =
                new LocalOptimizationRunner(configuration, new ComputationGraphTaskCreator());

        StatsStorage ss = new InMemoryStatsStorage();
        StatusListener sl = new ArbiterStatusListener(ss);
        runner.addListeners(sl);

        UIServer.getInstance().attach(ss);

        runner.execute();
        Thread.sleep(100000);
    }


    @Test
    @Ignore
    public void testCandidateGenerationExceptionsMnist() throws Exception {

        //Idea: Create a configuration that is not physically realizable, which should throw an exception
        // during the candidate generation phase
        //This exception should be visible in UI, but training should continue otherwise

        MultiLayerSpace mls = new MultiLayerSpace.Builder()
                .updater(new SgdSpace(new ContinuousParameterSpace(0.0001, 0.2)))
                .l2(new ContinuousParameterSpace(0.0001, 0.05))
                .dropOut(new ContinuousParameterSpace(0.2, 0.7))
                .addLayer(
                        new ConvolutionLayerSpace.Builder().nIn(1)
                                .nOut(new IntegerParameterSpace(5, 5))
                                .kernelSize(new DiscreteParameterSpace<>(new int[]{14, 14}, new int[]{30, 30}))
                                .stride(2, 2)
                                .activation(new DiscreteParameterSpace<>(Activation.RELU, Activation.SOFTPLUS, Activation.LEAKYRELU))
                                .build())
                .addLayer(new DenseLayerSpace.Builder().nOut(new IntegerParameterSpace(32, 128))
                        .activation(new DiscreteParameterSpace<>(Activation.RELU, Activation.TANH))
                        .build(), new IntegerParameterSpace(0, 1), true) //0 to 1 layers
                .addLayer(new OutputLayerSpace.Builder().nOut(10).activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT).build())
                .setInputType(InputType.convolutionalFlat(28, 28, 1))
                .build();
        Map<String, Object> commands = new HashMap<>();
//        commands.put(DataSetIteratorFactoryProvider.FACTORY_KEY, TestDataFactoryProviderMnist.class.getCanonicalName());

        //Define configuration:
        CandidateGenerator candidateGenerator = new RandomSearchGenerator(mls, commands);
        DataProvider dataProvider = new MnistDataSetProvider();


        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterUiTestBasicMnist\\").getAbsolutePath();

        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();
        f.mkdir();
        if (!f.exists())
            throw new RuntimeException();

        OptimizationConfiguration configuration =
                new OptimizationConfiguration.Builder()
                        .candidateGenerator(candidateGenerator).dataProvider(dataProvider)
                        .modelSaver(new FileModelSaver(modelSavePath))
                        .scoreFunction(new TestSetLossScoreFunction(true))
                        .terminationConditions(new MaxTimeCondition(120, TimeUnit.MINUTES),
                                new MaxCandidatesCondition(100))
                        .build();

        IOptimizationRunner runner =
                new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());

        StatsStorage ss = new InMemoryStatsStorage();
        StatusListener sl = new ArbiterStatusListener(ss);
        runner.addListeners(sl);

        UIServer.getInstance().attach(ss);

        runner.execute();
        Thread.sleep(1000_000);
    }


    @Test
    @Ignore
    public void testCandidateExecutionExceptionsMnist() throws Exception {
        //Idea: Create a configuration that will throw an exception in the *execution* stage
        // How? let's set wrong nOut
        //This exception should be visible in UI, but training should continue otherwise

        MultiLayerSpace mls = new MultiLayerSpace.Builder()
                .updater(new SgdSpace(new ContinuousParameterSpace(0.0001, 0.2)))
                .l2(new ContinuousParameterSpace(0.0001, 0.05))
                .dropOut(new ContinuousParameterSpace(0.2, 0.7))
                .addLayer(
                        new ConvolutionLayerSpace.Builder().nIn(1)
                                .nOut(new IntegerParameterSpace(5, 5))
                                .kernelSize(new DiscreteParameterSpace<>(new int[]{3, 3}, new int[]{4, 4}))
                                .stride(2, 2)
                                .activation(new DiscreteParameterSpace<>(Activation.RELU, Activation.SOFTPLUS, Activation.LEAKYRELU))
                                .build())
                .addLayer(new DenseLayerSpace.Builder().nOut(new IntegerParameterSpace(32, 64))
                        .activation(new DiscreteParameterSpace<>(Activation.RELU, Activation.TANH))
                        .build(), new IntegerParameterSpace(0, 1), true) //0 to 1 layers
                .addLayer(new OutputLayerSpace.Builder().nOut(99).activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT).build())
                .setInputType(InputType.convolutionalFlat(28, 28, 1))
                .build();
        Map<String, Object> commands = new HashMap<>();
//        commands.put(DataSetIteratorFactoryProvider.FACTORY_KEY, TestDataFactoryProviderMnist.class.getCanonicalName());

        //Define configuration:
        CandidateGenerator candidateGenerator = new RandomSearchGenerator(mls, commands);
        DataProvider dataProvider = new MnistDataSetProvider();


        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterUiTestBasicMnist\\").getAbsolutePath();

        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();
        f.mkdir();
        if (!f.exists())
            throw new RuntimeException();

        OptimizationConfiguration configuration =
                new OptimizationConfiguration.Builder()
                        .candidateGenerator(candidateGenerator).dataProvider(dataProvider)
                        .modelSaver(new FileModelSaver(modelSavePath))
                        .scoreFunction(new TestSetLossScoreFunction(true))
                        .terminationConditions(new MaxTimeCondition(120, TimeUnit.MINUTES),
                                new MaxCandidatesCondition(100))
                        .build();

        IOptimizationRunner runner =
                new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());

        StatsStorage ss = new InMemoryStatsStorage();
        StatusListener sl = new ArbiterStatusListener(ss);
        runner.addListeners(sl);

        UIServer.getInstance().attach(ss);

        runner.execute();
        Thread.sleep(1000_000);
    }


    @Test
    @Ignore
    public void testExecutionExceptionMnistCompGraph() throws Exception {

        //Idea: Create a configuration that will throw an exception in the *execution* stage
        // How? let's set wrong nOut
        //This exception should be visible in UI, but training should continue otherwise

        ComputationGraphSpace cgs = new ComputationGraphSpace.Builder()
                .updater(new SgdSpace(new ContinuousParameterSpace(0.0001, 0.2)))
                .l2(new ContinuousParameterSpace(0.0001, 0.05))
                .dropOut(new ContinuousParameterSpace(0.2, 0.7))
                .addInputs("in")
                .addLayer("0",
                        new ConvolutionLayerSpace.Builder().nIn(1)
                                .nOut(new IntegerParameterSpace(5, 30))
                                .kernelSize(new DiscreteParameterSpace<>(new int[]{3, 3},
                                        new int[]{4, 4}, new int[]{5, 5}))
                                .stride(new DiscreteParameterSpace<>(new int[]{1, 1},
                                        new int[]{2, 2}))
                                .activation(new DiscreteParameterSpace<>(Activation.RELU,
                                        Activation.SOFTPLUS, Activation.LEAKYRELU))
                                .build(), "in")
                .addLayer("1", new DenseLayerSpace.Builder().nOut(new IntegerParameterSpace(32, 64))
                        .activation(new DiscreteParameterSpace<>(Activation.RELU, Activation.TANH))
                        .build(), "0")
                .addLayer("out", new OutputLayerSpace.Builder().nIn(99).nOut(10).activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT).build(), "1")
                .setOutputs("out")
                .setInputTypes(InputType.convolutionalFlat(28, 28, 1))
                .build();

        //Define configuration:
        CandidateGenerator candidateGenerator = new RandomSearchGenerator(cgs);
        DataProvider dataProvider = new MnistDataSetProvider();


        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterUiTestBasicMnistCG\\").getAbsolutePath();

        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();
        f.mkdir();
        if (!f.exists())
            throw new RuntimeException();

        OptimizationConfiguration configuration =
                new OptimizationConfiguration.Builder()
                        .candidateGenerator(candidateGenerator).dataProvider(dataProvider)
                        .modelSaver(new FileModelSaver(modelSavePath))
                        .scoreFunction(new TestSetLossScoreFunction(true))
                        .terminationConditions(new MaxTimeCondition(120, TimeUnit.MINUTES),
                                new MaxCandidatesCondition(100))
                        .build();

        IOptimizationRunner runner =
                new LocalOptimizationRunner(configuration, new ComputationGraphTaskCreator());

        StatsStorage ss = new InMemoryStatsStorage();
        StatusListener sl = new ArbiterStatusListener(ss);
        runner.addListeners(sl);

        UIServer.getInstance().attach(ss);

        runner.execute();
        Thread.sleep(1000_000);
    }


    /**
     * Visualize multiple optimization sessions run one after another on single-session mode UI
     * @throws InterruptedException if current thread has been interrupted
     */
    @Test
    @Ignore
    public void testBasicMnistMultipleSessions() throws InterruptedException {

        MultiLayerSpace mls = new MultiLayerSpace.Builder()
                .updater(new SgdSpace(new ContinuousParameterSpace(0.0001, 0.2)))
                .l2(new ContinuousParameterSpace(0.0001, 0.05))
                .dropOut(new ContinuousParameterSpace(0.2, 0.7))
                .addLayer(
                        new ConvolutionLayerSpace.Builder().nIn(1)
                                .nOut(new IntegerParameterSpace(5, 30))
                                .kernelSize(new DiscreteParameterSpace<>(new int[]{3, 3},
                                        new int[]{4, 4}, new int[]{5, 5}))
                                .stride(new DiscreteParameterSpace<>(new int[]{1, 1},
                                        new int[]{2, 2}))
                                .activation(new DiscreteParameterSpace<>(Activation.RELU,
                                        Activation.SOFTPLUS, Activation.LEAKYRELU))
                                .build())
                .addLayer(new DenseLayerSpace.Builder().nOut(new IntegerParameterSpace(32, 128))
                        .activation(new DiscreteParameterSpace<>(Activation.RELU, Activation.TANH))
                        .build(), new IntegerParameterSpace(0, 1), true) //0 to 1 layers
                .addLayer(new OutputLayerSpace.Builder().nOut(10).activation(Activation.SOFTMAX)
                        .lossFunction(LossFunctions.LossFunction.MCXENT).build())
                .setInputType(InputType.convolutionalFlat(28, 28, 1))
                .build();
        Map<String, Object> commands = new HashMap<>();
//        commands.put(DataSetIteratorFactoryProvider.FACTORY_KEY, TestDataFactoryProviderMnist.class.getCanonicalName());

        //Define configuration:
        CandidateGenerator candidateGenerator = new RandomSearchGenerator(mls, commands);

        Class<? extends DataSource> ds = MnistDataSource.class;
        Properties dsp = new Properties();
        dsp.setProperty("minibatch", "8");

        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterUiTestBasicMnist\\").getAbsolutePath();

        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();
        f.mkdir();
        if (!f.exists())
            throw new RuntimeException();

        OptimizationConfiguration configuration =
                new OptimizationConfiguration.Builder()
                        .candidateGenerator(candidateGenerator).dataSource(ds, dsp)
                        .modelSaver(new FileModelSaver(modelSavePath))
                        .scoreFunction(new TestSetLossScoreFunction(true))
                        .terminationConditions(new MaxTimeCondition(1, TimeUnit.MINUTES),
                                new MaxCandidatesCondition(3))
                        .build();

        IOptimizationRunner runner =
                new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());

        StatsStorage ss = new InMemoryStatsStorage();


        StatusListener sl = new ArbiterStatusListener(ss);
        runner.addListeners(sl);

        UIServer.getInstance().attach(ss);
        runner.execute();


        candidateGenerator = new RandomSearchGenerator(mls, commands);
        configuration = new OptimizationConfiguration.Builder()
                .candidateGenerator(candidateGenerator).dataSource(ds, dsp)
                .modelSaver(new FileModelSaver(modelSavePath))
                .scoreFunction(new TestSetLossScoreFunction(true))
                .terminationConditions(new MaxTimeCondition(1, TimeUnit.MINUTES),
                        new MaxCandidatesCondition(3))
                .build();

        runner = new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());
        sl = new ArbiterStatusListener(ss);
        runner.addListeners(sl);

        UIServer.getInstance().attach(ss);

        runner.execute();

        Thread.sleep(1000_000);
    }

    /**
     * Auto-attach multiple optimization sessions to multi-session mode UI
     * @throws IOException if could not connect to the server
     */
    @Test
    public void testUiMultiSessionAutoAttach() throws IOException {

        //Define configuration:
        MultiLayerSpace mls = getMultiLayerSpaceMnist();
        CandidateGenerator candidateGenerator = new RandomSearchGenerator(mls);

        Class<? extends DataSource> ds = MnistDataSource.class;
        Properties dsp = new Properties();
        dsp.setProperty("minibatch", "8");

        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterUiTestMultiSessionAutoAttach\\")
                .getAbsolutePath();

        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();
        f.mkdir();
        if (!f.exists())
            throw new RuntimeException();

        OptimizationConfiguration configuration =
                new OptimizationConfiguration.Builder()
                        .candidateGenerator(candidateGenerator).dataSource(ds, dsp)
                        .modelSaver(new FileModelSaver(modelSavePath))
                        .scoreFunction(new TestSetLossScoreFunction(true))
                        .terminationConditions(new MaxTimeCondition(10, TimeUnit.SECONDS),
                                new MaxCandidatesCondition(1))
                        .build();

        IOptimizationRunner runner =
                new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());

        // add 3 different sessions to the same execution
        HashMap<String, StatsStorage> statsStorageForSession = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            StatsStorage ss = new InMemoryStatsStorage();
            @NonNull String sessionId = "sid" + i;
            statsStorageForSession.put(sessionId, ss);
            StatusListener sl = new ArbiterStatusListener(sessionId, ss);
            runner.addListeners(sl);
        }

        Function<String, StatsStorage> statsStorageProvider = statsStorageForSession::get;
        UIServer uIServer = UIServer.getInstance(true, statsStorageProvider);
        String serverAddress = uIServer.getAddress();

        runner.execute();

        for (String sessionId : statsStorageForSession.keySet()) {
            /*
             * Visiting /arbiter/:sessionId to auto-attach StatsStorage
             */
            String sessionUrl = sessionUrl(uIServer.getAddress(), sessionId);
            HttpURLConnection conn = (HttpURLConnection) new URL(sessionUrl).openConnection();
            conn.connect();

            log.info("Checking auto-attaching Arbiter session at {}", sessionUrl(serverAddress, sessionId));
            assertEquals(HttpResponseStatus.OK.code(), conn.getResponseCode());
            assertTrue(uIServer.isAttached(statsStorageForSession.get(sessionId)));
        }
    }

    /**
     * Attach multiple optimization sessions to multi-session mode UI by manually visiting session URL
     * @throws Exception if an error occurred
     */
    @Test
    @Ignore
    public void testUiMultiSessionManualAttach() throws Exception {
        Nd4j.setDefaultDataTypes(DataType.FLOAT, DataType.FLOAT);

        //Define configuration:
        MultiLayerSpace mls = getMultiLayerSpaceMnist();
        CandidateGenerator candidateGenerator = new RandomSearchGenerator(mls);

        Class<? extends DataSource> ds = MnistDataSource.class;
        Properties dsp = new Properties();
        dsp.setProperty("minibatch", "8");

        String modelSavePath = new File(System.getProperty("java.io.tmpdir"), "ArbiterUiTestBasicMnist\\")
                .getAbsolutePath();

        File f = new File(modelSavePath);
        if (f.exists())
            f.delete();
        f.mkdir();
        if (!f.exists())
            throw new RuntimeException();

        OptimizationConfiguration configuration =
                new OptimizationConfiguration.Builder()
                        .candidateGenerator(candidateGenerator).dataSource(ds, dsp)
                        .modelSaver(new FileModelSaver(modelSavePath))
                        .scoreFunction(new TestSetLossScoreFunction(true))
                        .terminationConditions(new MaxTimeCondition(10, TimeUnit.MINUTES),
                                new MaxCandidatesCondition(10))
                        .build();


        // parallel execution of multiple optimization sessions
        HashMap<String, StatsStorage> statsStorageForSession = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            String sessionId = "sid" + i;
            IOptimizationRunner runner =
                    new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());
            StatsStorage ss = new InMemoryStatsStorage();
            statsStorageForSession.put(sessionId, ss);
            StatusListener sl = new ArbiterStatusListener(sessionId, ss);
            runner.addListeners(sl);
            // Asynchronous execution
            new Thread(runner::execute).start();
        }

        Function<String, StatsStorage> statsStorageProvider = statsStorageForSession::get;
        UIServer uIServer = UIServer.getInstance(true, statsStorageProvider);
        String serverAddress = uIServer.getAddress();

        for (String sessionId : statsStorageForSession.keySet()) {
            log.info("Arbiter session can be attached at {}", sessionUrl(serverAddress, sessionId));
        }

        Thread.sleep(1000_000);
    }


    /**
     * Get URL for arbiter session on given server address
     * @param serverAddress server address, e.g.: http://localhost:9000
     * @param sessionId session ID (will be URL-encoded)
     * @return URL
     * @throws UnsupportedEncodingException if the character encoding is not supported
     */
    private static String sessionUrl(String serverAddress, String sessionId) throws UnsupportedEncodingException {
        return String.format("%s/arbiter/%s", serverAddress, URLEncoder.encode(sessionId, "UTF-8"));
    }

    private static class MnistDataSetProvider implements DataProvider {

        @Override
        public DataSetIterator trainData(Map<String, Object> dataParameters) {
            try {
                if (dataParameters == null || dataParameters.isEmpty()) {
                    return new MnistDataSetIterator(64, 10000, false, true, true, 123);
                }
                if (dataParameters.containsKey("batchsize")) {
                    int b = (Integer) dataParameters.get("batchsize");
                    return new MnistDataSetIterator(b, 10000, false, true, true, 123);
                }
                return new MnistDataSetIterator(64, 10000, false, true, true, 123);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public DataSetIterator testData(Map<String, Object> dataParameters) {
            return trainData(dataParameters);
        }

        @Override
        public Class<?> getDataType() {
            return DataSetIterator.class;
        }

        @Override
        public String toString() {
            return "MnistDataSetProvider()";
        }
    }

    public static class MnistDataSource implements DataSource {
        private int minibatch;

        public MnistDataSource() {

        }

        @Override
        public void configure(Properties properties) {
            this.minibatch = Integer.parseInt(properties.getProperty("minibatch", "16"));
        }

        @Override
        public Object trainData() {
            try {
                return new EarlyTerminationDataSetIterator(new MnistDataSetIterator(minibatch, true, 12345), 3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object testData() {
            try {
                return new EarlyTerminationDataSetIterator(new MnistDataSetIterator(minibatch, true, 12345), 3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<?> getDataType() {
            return DataSetIterator.class;
        }
    }

}
