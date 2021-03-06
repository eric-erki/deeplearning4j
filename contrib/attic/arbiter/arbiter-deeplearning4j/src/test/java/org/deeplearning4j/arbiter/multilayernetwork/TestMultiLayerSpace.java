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

package org.deeplearning4j.arbiter.multilayernetwork;

import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.arbiter.DL4JConfiguration;
import org.deeplearning4j.arbiter.MultiLayerSpace;
import org.deeplearning4j.arbiter.TestUtils;
import org.deeplearning4j.arbiter.conf.updater.AdamSpace;
import org.deeplearning4j.arbiter.conf.updater.NesterovsSpace;
import org.deeplearning4j.arbiter.conf.updater.SgdSpace;
import org.deeplearning4j.arbiter.layers.*;
import org.deeplearning4j.arbiter.optimize.api.Candidate;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.api.data.DataProvider;
import org.deeplearning4j.arbiter.optimize.api.saving.ResultSaver;
import org.deeplearning4j.arbiter.optimize.api.score.ScoreFunction;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxCandidatesCondition;
import org.deeplearning4j.arbiter.optimize.api.termination.TerminationCondition;
import org.deeplearning4j.arbiter.optimize.config.OptimizationConfiguration;
import org.deeplearning4j.arbiter.optimize.generator.GridSearchCandidateGenerator;
import org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator;
import org.deeplearning4j.arbiter.optimize.parameter.FixedValue;
import org.deeplearning4j.arbiter.optimize.parameter.continuous.ContinuousParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.integer.IntegerParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.math.MathOp;
import org.deeplearning4j.arbiter.optimize.parameter.math.Op;
import org.deeplearning4j.arbiter.optimize.runner.IOptimizationRunner;
import org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner;
import org.deeplearning4j.arbiter.saver.local.FileModelSaver;
import org.deeplearning4j.arbiter.scoring.impl.TestSetAccuracyScoreFunction;
import org.deeplearning4j.arbiter.task.MultiLayerNetworkTaskCreator;
import org.deeplearning4j.arbiter.util.LeafUtils;
import org.deeplearning4j.datasets.iterator.ExistingDataSetIterator;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.constraint.NonNegativeConstraint;
import org.deeplearning4j.nn.conf.constraint.UnitNormConstraint;
import org.deeplearning4j.nn.conf.dropout.Dropout;
import org.deeplearning4j.nn.conf.dropout.IDropout;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.BaseLayer;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.FeedForwardLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.conf.layers.variational.BernoulliReconstructionDistribution;
import org.deeplearning4j.nn.conf.layers.variational.GaussianReconstructionDistribution;
import org.deeplearning4j.nn.conf.layers.variational.ReconstructionDistribution;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.layers.recurrent.BidirectionalLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.ILossFunction;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.nd4j.linalg.lossfunctions.impl.LossMCXENT;
import org.nd4j.linalg.lossfunctions.impl.LossMSE;
import org.nd4j.common.primitives.Pair;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.*;

public class TestMultiLayerSpace extends BaseDL4JTest {

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @BeforeClass
    public static void before(){
        Nd4j.setDefaultDataTypes(DataType.FLOAT, DataType.FLOAT);
    }

    @Test
    public void testBasic() {

        MultiLayerConfiguration expected =
                        new NeuralNetConfiguration.Builder()
                                        .updater(new Sgd(0.005)).seed(12345).list()
                                        .layer(0, new DenseLayer.Builder().nIn(10).nOut(10).build())
                                        .layer(1, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(2,
                                                        new OutputLayer.Builder().lossFunction(LossFunction.MCXENT)
                                                                .activation(Activation.SOFTMAX).nIn(10).nOut(5).build())

                                        .build();

        MultiLayerSpace mls =
                        new MultiLayerSpace.Builder()
                                        .updater(new Sgd(0.005)).seed(12345)
                                        .addLayer(new DenseLayerSpace.Builder().nIn(10).nOut(10).build(),
                                                        new FixedValue<>(2)) //2 identical layers
                                        .addLayer(new OutputLayerSpace.Builder().lossFunction(LossFunction.MCXENT)
                                                .activation(Activation.SOFTMAX)
                                                        .nIn(10).nOut(5).build()).build();

        int nParams = mls.numParameters();
        assertEquals(0, nParams);

        MultiLayerConfiguration conf = mls.getValue(new double[0]).getMultiLayerConfiguration();

        assertEquals(expected, conf);
    }

    @Test
    public void testBasic0() {
        MultiLayerConfiguration expected =
                new NeuralNetConfiguration.Builder()
                        .l1Bias(0.4)
                        .l2Bias(0.5)
                        .constrainBias(new NonNegativeConstraint())
                        .updater(new Sgd(0.005)).seed(12345).list()
                        .layer(0, new DenseLayer.Builder().l1Bias(0.6).nIn(10).nOut(10).build())
                        .layer(1, new DenseLayer.Builder().l2Bias(0.7).constrainBias(new UnitNormConstraint()).nIn(10).nOut(10).build()).layer(2,
                        new OutputLayer.Builder().lossFunction(LossFunction.MCXENT).activation(Activation.SOFTMAX)
                                .nIn(10).nOut(5).build())
                        .build();

        MultiLayerSpace mls =
                new MultiLayerSpace.Builder()
                        .l1Bias(0.4)
                        .l2Bias(0.5)
                        .constrainBias(new NonNegativeConstraint())
                        .updater(new Sgd(0.005)).seed(12345)
                        .addLayer(new DenseLayerSpace.Builder().l1Bias(new ContinuousParameterSpace(0,1)).nIn(10).nOut(10).build())
                        .addLayer(new DenseLayerSpace.Builder().l2Bias(0.7).constrainBias(new UnitNormConstraint()).nIn(10).nOut(10).build())
                        .addLayer(new OutputLayerSpace.Builder().lossFunction(LossFunction.MCXENT).activation(Activation.SOFTMAX)
                                .nIn(10).nOut(5).build())
                        .build();

        int nParams = mls.numParameters();
        assertEquals(1, nParams);

        //Assign numbers to each leaf ParameterSpace object (normally done by candidate generator - manual here for testing)
        List<ParameterSpace> noDuplicatesList = LeafUtils.getUniqueObjects(mls.collectLeaves());

        //Second: assign each a number
        int c = 0;
        for (ParameterSpace ps : noDuplicatesList) {
            int np = ps.numParameters();
            if (np == 1) {
                ps.setIndices(c++);
            } else {
                int[] values = new int[np];
                for (int j = 0; j < np; j++)
                    values[c++] = j;
                ps.setIndices(values);
            }
        }
        MultiLayerConfiguration conf = mls.getValue(new double[] {0.6}).getMultiLayerConfiguration();

        assertEquals(expected, conf);
    }

    @Test
    public void testILossFunctionGetsSet() {
        ILossFunction lossFunction = new LossMCXENT(Nd4j.create(new float[] {1f, 2f}, new long[]{1,2}));

        MultiLayerConfiguration expected =
                        new NeuralNetConfiguration.Builder().updater(new Sgd(0.005)).seed(12345).list()
                                        .layer(0, new DenseLayer.Builder().nIn(10).nOut(10).build())
                                        .layer(1, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(2,
                                                        new OutputLayer.Builder().lossFunction(lossFunction)
                                                                .activation(Activation.SOFTMAX).nIn(10).nOut(5).build())
                                        .build();

        MultiLayerSpace mls = new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345)
                        .addLayer(new DenseLayerSpace.Builder().nIn(10).nOut(10).build(), new FixedValue<>(2)) //2 identical layers
                        .addLayer(new OutputLayerSpace.Builder().iLossFunction(lossFunction).activation(Activation.SOFTMAX).nIn(10).nOut(5).build())
                        .build();

        int nParams = mls.numParameters();
        assertEquals(0, nParams);

        MultiLayerConfiguration conf = mls.getValue(new double[0]).getMultiLayerConfiguration();

        assertEquals(expected, conf);
    }

    @Test
    public void testBasic2() {

        MultiLayerSpace mls =
                        new MultiLayerSpace.Builder().updater(new SgdSpace(new ContinuousParameterSpace(0.0001, 0.1)))
                                        .l2(new ContinuousParameterSpace(0.2, 0.5))
                                        .convolutionMode(ConvolutionMode.Same)
                                        .addLayer(new ConvolutionLayerSpace.Builder().nIn(3).nOut(3).kernelSize(2, 2)
                                                        .stride(1, 1).build())
                                        .addLayer(new DenseLayerSpace.Builder().nIn(10).nOut(10)
                                                        .activation(new DiscreteParameterSpace<>(Activation.RELU,
                                                                        Activation.TANH))
                                                        .build(), new IntegerParameterSpace(1, 3)) //1-3 identical layers
                                        .addLayer(new OutputLayerSpace.Builder().nIn(10).nOut(10)
                                                        .activation(Activation.SOFTMAX).build())
                                        .build();

        int nParams = mls.numParameters();
        assertEquals(4, nParams);

        //Assign numbers to each leaf ParameterSpace object (normally done by candidate generator - manual here for testing)
        List<ParameterSpace> noDuplicatesList = LeafUtils.getUniqueObjects(mls.collectLeaves());

        //Second: assign each a number
        int c = 0;
        for (ParameterSpace ps : noDuplicatesList) {
            int np = ps.numParameters();
            if (np == 1) {
                ps.setIndices(c++);
            } else {
                int[] values = new int[np];
                for (int j = 0; j < np; j++)
                    values[c++] = j;
                ps.setIndices(values);
            }
        }


        int[] nLayerCounts = new int[3];
        int reluCount = 0;
        int tanhCount = 0;

        Random r = new Random(12345);

        for (int i = 0; i < 50; i++) {

            double[] rvs = new double[nParams];
            for (int j = 0; j < rvs.length; j++)
                rvs[j] = r.nextDouble();


            MultiLayerConfiguration conf = mls.getValue(rvs).getMultiLayerConfiguration();

            int nLayers = conf.getConfs().size();
            assertTrue(nLayers >= 3 && nLayers <= 5); //1 conv + 1-3 dense layers + 1 output layer: 2 to 4

            int nLayersExOutputLayer = nLayers - 1;
            nLayerCounts[nLayersExOutputLayer - 2]++;

            for (int j = 0; j < nLayers; j++) {
                NeuralNetConfiguration layerConf = conf.getConf(j);

                double lr = ((Sgd)((BaseLayer) layerConf.getLayer()).getIUpdater()).getLearningRate();
                assertTrue(lr >= 0.0001 && lr <= 0.1);
                double l2 = TestUtils.getL2((BaseLayer) layerConf.getLayer());
                assertTrue(l2 >= 0.2 && l2 <= 0.5);

                if (j == nLayers - 1) { //Output layer
                    assertEquals(Activation.SOFTMAX.getActivationFunction(), ((BaseLayer) layerConf.getLayer()).getActivationFn());
                } else if (j == 0) {
                    //Conv layer
                    ConvolutionLayer cl = (ConvolutionLayer) layerConf.getLayer();
                    assertEquals(3, cl.getNIn());
                    assertEquals(3, cl.getNOut());
                    assertEquals(ConvolutionMode.Same, cl.getConvolutionMode());
                } else {
                    IActivation actFn = ((BaseLayer) layerConf.getLayer()).getActivationFn();
                    assertTrue(Activation.RELU.getActivationFunction().equals(actFn) ||
                            Activation.TANH.getActivationFunction().equals(actFn));
                    if (Activation.RELU.getActivationFunction().equals(actFn))
                        reluCount++;
                    else
                        tanhCount++;
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            assertTrue(nLayerCounts[i] >= 5); //Expect approx equal (50/3 each), but some variation randomly
        }

//        System.out.println("Number of layers: " + Arrays.toString(nLayerCounts));
//        System.out.println("ReLU vs. Tanh: " + reluCount + "\t" + tanhCount);

    }

    @Test
    public void testGlobalPoolingBasic() {

        MultiLayerConfiguration expected = new NeuralNetConfiguration.Builder().updater(new Sgd(0.005)).seed(12345).list()
                        .layer(0, new GravesLSTM.Builder().nIn(10).nOut(10).build())
                        .layer(1, new GlobalPoolingLayer.Builder().poolingType(PoolingType.SUM).pnorm(7).build())
                        .layer(2, new OutputLayer.Builder().lossFunction(LossFunction.MCXENT).activation(Activation.SOFTMAX).nIn(10).nOut(5).build())
                        .build();

        MultiLayerSpace mls =
                        new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345)
                                        .addLayer(new GravesLSTMLayerSpace.Builder().nIn(10).nOut(10).build())
                                        .addLayer(new GlobalPoolingLayerSpace.Builder().poolingType(PoolingType.SUM)
                                                        .pNorm(7).build())
                                        .addLayer(new OutputLayerSpace.Builder().lossFunction(LossFunction.MCXENT)
                                                .activation(Activation.SOFTMAX)
                                                        .nIn(10).nOut(5).build())
                                        .build();

        int nParams = mls.numParameters();
        assertEquals(0, nParams);

        MultiLayerConfiguration conf = mls.getValue(new double[0]).getMultiLayerConfiguration();

        assertEquals(expected, conf);
    }


    @Test
    public void testVariationalAutoencoderLayerSpaceBasic() {
        MultiLayerSpace mls =
                        new MultiLayerSpace.Builder()
                                        .updater(new Sgd(0.005)).seed(
                                                        12345)
                                        .addLayer(new VariationalAutoencoderLayerSpace.Builder()
                                                        .nIn(new IntegerParameterSpace(50, 75)).nOut(200)
                                                        .encoderLayerSizes(234, 567).decoderLayerSizes(123, 456)
                                                        .reconstructionDistribution(
                                                                        new DiscreteParameterSpace<ReconstructionDistribution>(
                                                                                        new GaussianReconstructionDistribution(),
                                                                                        new BernoulliReconstructionDistribution()))
                                                        .build())
                                        .build();

        int numParams = mls.numParameters();

        //Assign numbers to each leaf ParameterSpace object (normally done by candidate generator - manual here for testing)
        List<ParameterSpace> noDuplicatesList = LeafUtils.getUniqueObjects(mls.collectLeaves());

        //Second: assign each a number
        int c = 0;
        for (ParameterSpace ps : noDuplicatesList) {
            int np = ps.numParameters();
            if (np == 1) {
                ps.setIndices(c++);
            } else {
                int[] values = new int[np];
                for (int j = 0; j < np; j++)
                    values[c++] = j;
                ps.setIndices(values);
            }
        }

        double[] zeros = new double[numParams];

        DL4JConfiguration configuration = mls.getValue(zeros);

        MultiLayerConfiguration conf = configuration.getMultiLayerConfiguration();
        assertEquals(1, conf.getConfs().size());

        NeuralNetConfiguration nnc = conf.getConf(0);
        VariationalAutoencoder vae = (VariationalAutoencoder) nnc.getLayer();

        assertEquals(50, vae.getNIn());
        assertEquals(200, vae.getNOut());

        assertArrayEquals(new int[] {234, 567}, vae.getEncoderLayerSizes());
        assertArrayEquals(new int[] {123, 456}, vae.getDecoderLayerSizes());

        assertTrue(vae.getOutputDistribution() instanceof GaussianReconstructionDistribution);



        double[] ones = new double[numParams];
        Arrays.fill(ones, 1.0);

        configuration = mls.getValue(ones);

        conf = configuration.getMultiLayerConfiguration();
        assertEquals(1, conf.getConfs().size());

        nnc = conf.getConf(0);
        vae = (VariationalAutoencoder) nnc.getLayer();

        assertEquals(75, vae.getNIn());
        assertEquals(200, vae.getNOut());

        assertArrayEquals(new int[] {234, 567}, vae.getEncoderLayerSizes());
        assertArrayEquals(new int[] {123, 456}, vae.getDecoderLayerSizes());

        assertTrue(vae.getOutputDistribution() instanceof BernoulliReconstructionDistribution);
    }

    @Test
    public void testInputTypeBasic() throws Exception {

        ParameterSpace<Integer> layerSizeHyperparam = new IntegerParameterSpace(20, 60);

        MultiLayerSpace hyperparameterSpace = new MultiLayerSpace.Builder().l2(0.0001)
                        .weightInit(WeightInit.XAVIER).updater(new Nesterovs())
                        .addLayer(new ConvolutionLayerSpace.Builder().kernelSize(5, 5).nIn(1).stride(1, 1)
                                        .nOut(layerSizeHyperparam).activation(Activation.IDENTITY).build())
                        .addLayer(new SubsamplingLayerSpace.Builder().poolingType(SubsamplingLayer.PoolingType.MAX)
                                        .kernelSize(2, 2).stride(2, 2).build())
                        .addLayer(new ConvolutionLayerSpace.Builder().kernelSize(5, 5)
                                        //Note that nIn need not be specified in later layers
                                        .stride(1, 1).nOut(50).activation(Activation.IDENTITY).build())
                        .addLayer(new SubsamplingLayerSpace.Builder().poolingType(SubsamplingLayer.PoolingType.MAX)
                                        .kernelSize(2, 2).stride(2, 2).build())
                        .addLayer(new DenseLayerSpace.Builder().activation(Activation.RELU).nOut(500).build())
                        .addLayer(new OutputLayerSpace.Builder()
                                        .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).nOut(10)
                                        .activation(Activation.SOFTMAX).build())
                        .setInputType(InputType.convolutionalFlat(28, 28, 1)).build();


        DataProvider dataProvider = new TestDataSetProvider();

        File f = testDir.newFolder();
        if (f.exists())
            f.delete();
        f.mkdir();
        ResultSaver modelSaver = new FileModelSaver(f.getAbsolutePath());

        ScoreFunction scoreFunction = new TestSetAccuracyScoreFunction();

        int maxCandidates = 4;
        TerminationCondition[] terminationConditions;
        terminationConditions = new TerminationCondition[] {new MaxCandidatesCondition(maxCandidates)};

        //Given these configuration options, let's put them all together:
        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder()
                        .candidateGenerator(new RandomSearchGenerator(hyperparameterSpace, null))
                        .dataProvider(dataProvider).modelSaver(modelSaver).scoreFunction(scoreFunction)
                        .terminationConditions(terminationConditions).build();

        IOptimizationRunner runner = new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());
        runner.execute();

        assertEquals(maxCandidates, runner.getResults().size());
    }


    @Test
    public void testSameRanges() {

        ParameterSpace<Double> l1Hyperparam = new ContinuousParameterSpace(0.001, 0.1);
        ParameterSpace<Double> l2Hyperparam = new ContinuousParameterSpace(0.001, 0.1);

        MultiLayerSpace hyperparameterSpace =
                        new MultiLayerSpace.Builder().addLayer(new DenseLayerSpace.Builder().nIn(10).nOut(10).build())
                                        .l1(l1Hyperparam).l2(l2Hyperparam).build();

        CandidateGenerator c = new RandomSearchGenerator(hyperparameterSpace, null);

        Candidate candidate = c.getCandidate();
    }

    @Test
    public void testWeightedLossFunction() {

        MultiLayerConfiguration expected =
                        new NeuralNetConfiguration.Builder().updater(new Sgd(0.005)).seed(12345).list()
                                        .layer(0, new DenseLayer.Builder().nIn(10).nOut(10).build())
                                        .layer(1, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(2,
                                                        new OutputLayer.Builder()
                                                                        .lossFunction(new LossMSE(Nd4j.create(
                                                                                        new double[] {1, 2, 3, 4, 5}, new long[]{1,5})))
                                                                        .nIn(10).nOut(5).build())
                                        .build();

        MultiLayerSpace mls =
                        new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345)
                                        .addLayer(new DenseLayerSpace.Builder().nIn(10).nOut(10).build(),
                                                        new FixedValue<>(2)) //2 identical layers
                                        .addLayer(new OutputLayerSpace.Builder()
                                                        .iLossFunction(new LossMSE(Nd4j.create(new double[] {1, 2, 3, 4, 5}, new long[]{1,5})))
                                                        .nIn(10).nOut(5).build())
                                        .build();

        int nParams = mls.numParameters();
        assertEquals(0, nParams);

        MultiLayerConfiguration conf = mls.getValue(new double[0]).getMultiLayerConfiguration();

        assertEquals(expected, conf);

        String json = mls.toJson();
        MultiLayerSpace fromJson = MultiLayerSpace.fromJson(json);

        assertEquals(mls, fromJson);
    }


    @Test
    public void testBidirectional() throws Exception {

        MultiLayerSpace mls =
                new MultiLayerSpace.Builder().updater(new Sgd(0.005))
                        .seed(12345)
                        .layer(new Bidirectional(new LSTMLayerSpace.Builder()
                                .nIn(10).nOut(10).build()))
                        .build();

        DL4JConfiguration conf = mls.getValue(new double[0]);
        MultiLayerConfiguration c2 = conf.getMultiLayerConfiguration();

        MultiLayerNetwork net = new MultiLayerNetwork(c2);
        net.init();

        assertEquals(1, net.getnLayers());
        assertTrue(net.getLayer(0) instanceof BidirectionalLayer);
        BidirectionalLayer bl = (BidirectionalLayer)net.getLayer(0);

        Field f = BidirectionalLayer.class.getDeclaredField("fwd");
        Field b = BidirectionalLayer.class.getDeclaredField("bwd");
        f.setAccessible(true);
        b.setAccessible(true);
        org.deeplearning4j.nn.layers.recurrent.LSTM lstmFwd = (org.deeplearning4j.nn.layers.recurrent.LSTM) f.get(bl);
        org.deeplearning4j.nn.layers.recurrent.LSTM lstmBwd = (org.deeplearning4j.nn.layers.recurrent.LSTM) b.get(bl);

        assertEquals(10, ((LSTM)lstmFwd.conf().getLayer()).getNIn());
        assertEquals(10, ((LSTM)lstmFwd.conf().getLayer()).getNOut());
        assertEquals(10, ((LSTM)lstmBwd.conf().getLayer()).getNIn());
        assertEquals(10, ((LSTM)lstmBwd.conf().getLayer()).getNOut());
    }


    @Test
    public void testMathOps() {

        ParameterSpace<Integer> firstLayerSize = new IntegerParameterSpace(10,30);
        ParameterSpace<Integer> secondLayerSize = new MathOp<>(firstLayerSize, Op.MUL, 3);
        ParameterSpace<Double> firstLayerLR = new ContinuousParameterSpace(0.01, 0.1);
        ParameterSpace<Double> secondLayerLR = new MathOp<>(firstLayerLR, Op.ADD, 0.2);

        MultiLayerSpace mls =
                new MultiLayerSpace.Builder().updater(new Sgd(0.005))
                        .seed(12345)
                        .layer(new DenseLayerSpace.Builder().nOut(firstLayerSize)
                                .updater(new AdamSpace(firstLayerLR))
                                .build())
                        .layer(new OutputLayerSpace.Builder().nOut(secondLayerSize)
                                .updater(new AdamSpace(secondLayerLR))
                                .activation(Activation.SOFTMAX)
                                .build())
                        .setInputType(InputType.feedForward(10))
                        .build();

        int nParams = mls.numParameters();
        assertEquals(2, nParams);

        new RandomSearchGenerator(mls, null);    //Initializes the indices

        Random r = new Random(12345);
        for( int i=0; i<10; i++ ){
            double[] d = new double[nParams];
            for( int j=0; j<d.length; j++ ){
                d[j] = r.nextDouble();
            }

            MultiLayerConfiguration conf = mls.getValue(d).getMultiLayerConfiguration();
            long l0Size = ((FeedForwardLayer)conf.getConf(0).getLayer()).getNOut();
            long l1Size = ((FeedForwardLayer)conf.getConf(1).getLayer()).getNOut();
            assertEquals(3*l0Size, l1Size);

            double l0Lr = ((FeedForwardLayer)conf.getConf(0).getLayer()).getIUpdater().getLearningRate(0,0);
            double l1Lr = ((FeedForwardLayer)conf.getConf(1).getLayer()).getIUpdater().getLearningRate(0,0);
            assertEquals(l0Lr+0.2, l1Lr, 1e-6);
        }
    }

    @Test
    public void testDropoutSpace(){

        ParameterSpace<Double> dropout = new DiscreteParameterSpace<>(0.0, 0.5);

        MultiLayerSpace mls =
                new MultiLayerSpace.Builder().updater(new Sgd(0.005))
                        .dropOut(dropout)
                        .seed(12345)
                        .layer(new DenseLayerSpace.Builder().nOut(10)
                                .build())
                        .layer(new OutputLayerSpace.Builder().nOut(10).activation(Activation.SOFTMAX)
                                .build())
                        .setInputType(InputType.feedForward(10))
                        .build();

        int nParams = mls.numParameters();
        assertEquals(1, nParams);

        new RandomSearchGenerator(mls, null);    //Initializes the indices

        Random r = new Random(12345);
        int countNull = 0;
        int count05 = 0;
        for( int i=0; i<10; i++ ){
            double[] d = new double[nParams];
            for( int j=0; j<d.length; j++ ){
                d[j] = r.nextDouble();
            }

            MultiLayerConfiguration conf = mls.getValue(d).getMultiLayerConfiguration();
            IDropout d0 = conf.getConf(0).getLayer().getIDropout();
            IDropout d1 = conf.getConf(1).getLayer().getIDropout();

            if(d0 == null){
                assertNull(d1);
                countNull++;
            } else {
                Dropout do0 = (Dropout)d0;
                Dropout do1 = (Dropout)d1;

                assertEquals(0.5, do0.getP(), 0.0);
                assertEquals(0.5, do1.getP(), 0.0);
                count05++;
            }
        }
        assertTrue(countNull > 0);
        assertTrue(count05 > 0);
    }


    private static class TestDataSetProvider implements DataProvider {

        @Override
        public Object trainData(Map<String, Object> dataParameters) {
            return new ExistingDataSetIterator(
                    Collections.singletonList(new DataSet(Nd4j.create(1, 1, 28, 28), Nd4j.create(1,10))));
        }

        @Override
        public Object testData(Map<String, Object> dataParameters) {
            return new ExistingDataSetIterator(
                    Collections.singletonList(new DataSet(Nd4j.create(1, 1, 28, 28), Nd4j.create(1,10))));
        }

        @Override
        public Class<?> getDataType() {
            return DataSetIterator.class;
        }
    }


    @Test
    public void testDropout(){

        MultiLayerSpace mls = new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345)
                .addLayer(new ConvolutionLayerSpace.Builder().nOut(2)
                        .dropOut(new ContinuousParameterSpace(0.4,0.6))
                        .build())
                .addLayer(new GlobalPoolingLayerSpace.Builder().dropOut(new ContinuousParameterSpace(0.4,0.6)).build())
                .addLayer(new OutputLayerSpace.Builder().activation(Activation.SOFTMAX).nIn(10).nOut(5).build())
                .setInputType(InputType.convolutional(28, 28, 1))
                .build();

        int nParams = mls.numParameters();
        List<ParameterSpace> l = LeafUtils.getUniqueObjects(mls.collectLeaves());
        int x=0;
        for( ParameterSpace p : l){
            int n = p.numParameters();
            int[] arr = new int[n];
            for(int i=0; i<arr.length; i++ ){
                arr[i] = x++;
            }
            p.setIndices(arr);
        }


        MultiLayerConfiguration conf = mls.getValue(new double[nParams]).getMultiLayerConfiguration();
    }

    @Test
    public void testDropout2(){

        MultiLayerSpace mls = new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345)
                .addLayer(new ConvolutionLayerSpace.Builder().nOut(2)
                        .dropOut(new ContinuousParameterSpace(0.4,0.6))
                        .build())
                .addLayer(new DropoutLayerSpace.Builder().dropOut(new ContinuousParameterSpace(0.4,0.6)).build())
                .addLayer(new OutputLayerSpace.Builder().activation(Activation.SOFTMAX).nIn(10).nOut(5).build())
                .setInputType(InputType.convolutional(28, 28, 1))
                .build();

        int nParams = mls.numParameters();
        List<ParameterSpace> l = LeafUtils.getUniqueObjects(mls.collectLeaves());
        int x=0;
        for( ParameterSpace p : l){
            int n = p.numParameters();
            int[] arr = new int[n];
            for(int i=0; i<arr.length; i++ ){
                arr[i] = x++;
            }
            p.setIndices(arr);
        }


        MultiLayerConfiguration conf = mls.getValue(new double[nParams]).getMultiLayerConfiguration();
    }


    @Test
    public void testIssue8082(){
        ParameterSpace<Double> learningRateHyperparam = new DiscreteParameterSpace<>(0.003, 0.005, 0.01, 0.05);
        ParameterSpace<Integer> layerSizeHyperparam1 = new DiscreteParameterSpace<>(32, 64, 96, 128);
        ParameterSpace<Integer> layerSizeHyperparam2 = new DiscreteParameterSpace<>(32, 64, 96, 128);
        ParameterSpace<Double> dropoutHyperparam = new DiscreteParameterSpace<>(0.8, 0.9);

        MultiLayerSpace mls = new MultiLayerSpace.Builder()
                .updater(new AdamSpace(learningRateHyperparam))
                .weightInit(WeightInit.XAVIER)
                .l2(0.0001)
                .addLayer(new DenseLayerSpace.Builder()
                        .nIn(10)
                        .nOut(layerSizeHyperparam1)
                        .build())
                .addLayer(new BatchNormalizationSpace.Builder()
                        .nOut(layerSizeHyperparam1)
                        .activation(Activation.RELU)
                        .build())
                .addLayer(new DropoutLayerSpace.Builder()
                        .dropOut(dropoutHyperparam)
                        .build())
                .addLayer(new DenseLayerSpace.Builder()
                        .nOut(layerSizeHyperparam2)
                        .build())
                .addLayer(new BatchNormalizationSpace.Builder()
                        .nOut(layerSizeHyperparam2)
                        .activation(Activation.RELU)
                        .build())
                .addLayer(new DropoutLayerSpace.Builder()
                        .dropOut(dropoutHyperparam)
                        .build())
                .addLayer(new OutputLayerSpace.Builder()
                        .nOut(10)
                        .activation(Activation.SOFTMAX)
                        .lossFunction(LossFunction.MCXENT)
                        .build())
                .build();

        assertEquals(4, mls.getNumParameters());

        for( int discreteCount : new int[]{1, 5}) {
            GridSearchCandidateGenerator generator = new GridSearchCandidateGenerator(mls, discreteCount, GridSearchCandidateGenerator.Mode.Sequential, null);

            int expCandidates = 4 * 4 * 4 * 2;
            assertEquals(expCandidates, generator.getTotalNumCandidates());

            int count = 0;
            while (generator.hasMoreCandidates()) {
                generator.getCandidate();
                count++;
            }


            assertEquals(expCandidates, count);
        }
    }


    @Test
    public void testGridCandidateGenerator(){
        ParameterSpace<Integer> layerSizeParam = new DiscreteParameterSpace<>(32, 48, 64);
        ParameterSpace<Double> learningRateParam = new DiscreteParameterSpace<>(0.005, 0.007, 0.01);

        MultiLayerSpace hyperParamaterSpace = new MultiLayerSpace.Builder()
                .seed(12345)
                .biasInit(1)
                .l2(1e-4)
                .updater(new NesterovsSpace(learningRateParam))
                .addLayer(new DenseLayerSpace.Builder().nIn(10).nOut(layerSizeParam)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .addLayer(new DenseLayerSpace.Builder().nIn(layerSizeParam).nOut(layerSizeParam)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.RELU)
                        .build())
                .addLayer(new OutputLayerSpace.Builder()
                        .lossFunction(LossFunctions.LossFunction.MSE)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX)
                        .nIn(layerSizeParam).nOut(10).build())
                .build();

        CandidateGenerator candidateGenerator = new GridSearchCandidateGenerator(hyperParamaterSpace, 30, GridSearchCandidateGenerator.Mode.Sequential, null);
//        CandidateGenerator candidateGenerator = new RandomSearchGenerator(hyperParamaterSpace);

        Set<Pair<Double,Integer>> expCandidates = new HashSet<>();
        for(Double d : new double[]{0.005, 0.007, 0.01}){
            for(int i : new int[]{32, 48, 64}){
                expCandidates.add(new Pair<>(d, i));
            }
        }

        Set<Pair<Double,Integer>> actCandidates = new HashSet<>();
        while(candidateGenerator.hasMoreCandidates()) {
            Candidate<DL4JConfiguration> conf = candidateGenerator.getCandidate();
            MultiLayerConfiguration mlc = conf.getValue().getMultiLayerConfiguration();
            FeedForwardLayer ffl = ((FeedForwardLayer) mlc.getConf(0).getLayer());
            actCandidates.add(new Pair<>(ffl.getIUpdater().getLearningRate(0,0), (int)ffl.getNOut()));
        }

        assertEquals(expCandidates, actCandidates);
    }
}
