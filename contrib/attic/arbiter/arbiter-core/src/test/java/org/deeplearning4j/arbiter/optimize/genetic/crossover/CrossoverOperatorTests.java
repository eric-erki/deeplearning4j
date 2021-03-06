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

package org.deeplearning4j.arbiter.optimize.genetic.crossover;

import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.arbiter.optimize.generator.genetic.population.PopulationInitializer;
import org.deeplearning4j.arbiter.optimize.generator.genetic.population.PopulationModel;
import org.deeplearning4j.arbiter.optimize.genetic.TestCrossoverOperator;
import org.deeplearning4j.arbiter.optimize.genetic.TestPopulationInitializer;
import org.junit.Assert;
import org.junit.Test;

public class CrossoverOperatorTests extends BaseDL4JTest {

    @Test
    public void CrossoverOperator_initializeInstance_ShouldInitPopulationModel() throws IllegalAccessException {
        TestCrossoverOperator sut = new TestCrossoverOperator(null);

        PopulationInitializer populationInitializer = new TestPopulationInitializer();

        PopulationModel populationModel =
                        new PopulationModel.Builder().populationInitializer(populationInitializer).build();
        sut.initializeInstance(populationModel);

        Assert.assertSame(populationModel, sut.getPopulationModel());


    }
}
