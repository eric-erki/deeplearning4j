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

package org.deeplearning4j.arbiter.scoring.impl;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.deeplearning4j.arbiter.scoring.RegressionValue;
import org.deeplearning4j.arbiter.scoring.util.ScoreUtil;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;

/**
 * Score function for regression (including multi-label regression) for a MultiLayerNetwork or ComputationGraph
 * on a test set
 *
 * @author Alex Black
 * @deprecated Use {@link RegressionScoreFunction}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)  //For JSON
@Deprecated
public class TestSetRegressionScoreFunction extends BaseNetScoreFunction {
    private RegressionValue regressionValue;

    /**
     * @param regressionValue The type of evaluation to do: MSE, MAE, RMSE, etc
     */
    public TestSetRegressionScoreFunction(RegressionValue regressionValue) {
        this.regressionValue = regressionValue;
    }


    @Override
    public boolean minimize() {
        return regressionValue != RegressionValue.CorrCoeff; //Maximize correlation coefficient, minimize the remaining ones
    }

    @Override
    public String toString() {
        return "TestSetRegressionScoreFunction(type=" + regressionValue + ")";
    }

    @Override
    public double score(MultiLayerNetwork net, DataSetIterator iterator) {
        RegressionEvaluation e = net.evaluateRegression(iterator);
        return ScoreUtil.getScoreFromRegressionEval(e, regressionValue);
    }

    @Override
    public double score(MultiLayerNetwork net, MultiDataSetIterator iterator) {
        throw new UnsupportedOperationException("Cannot evaluate MultiLayerNetwork on MultiDataSetIterator");
    }

    @Override
    public double score(ComputationGraph graph, DataSetIterator iterator) {
        RegressionEvaluation e = graph.evaluateRegression(iterator);
        return ScoreUtil.getScoreFromRegressionEval(e, regressionValue);
    }

    @Override
    public double score(ComputationGraph graph, MultiDataSetIterator iterator) {
        RegressionEvaluation e = graph.evaluateRegression(iterator);
        return ScoreUtil.getScoreFromRegressionEval(e, regressionValue);
    }
}
