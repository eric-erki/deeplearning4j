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

package org.deeplearning4j.arbiter.optimize.generator.genetic.population;

import org.deeplearning4j.arbiter.optimize.generator.genetic.Chromosome;

import java.util.List;

/**
 * A listener that is called when the population changes.
 *
 * @author Alexandre Boulanger
 */
public interface PopulationListener {
    /**
     * Called after the population has changed.
     *
     * @param population The population after it has changed.
     */
    void onChanged(List<Chromosome> population);
}
