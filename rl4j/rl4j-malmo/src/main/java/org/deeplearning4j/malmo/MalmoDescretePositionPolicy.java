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

package org.deeplearning4j.malmo;

import com.microsoft.msr.malmo.WorldState;
import org.deeplearning4j.rl4j.space.Box;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * A Malmo consistency policy that ensures the both there is a reward and next observation has a different position that the previous one.
 * This will only work for your mission if you require that every action moves to a new location. 
 * @author howard-abrams (howard.abrams@ca.com) on 1/12/17.
 */
public class MalmoDescretePositionPolicy implements MalmoObservationPolicy {
    MalmoObservationSpacePosition observationSpace = new MalmoObservationSpacePosition();

    @Override
    public boolean isObservationConsistant(WorldState world_state, WorldState original_world_state) {
        Box last_observation = observationSpace.getObservation(world_state);
        Box old_observation = observationSpace.getObservation(original_world_state);

        INDArray newvalues = old_observation == null ? null : old_observation.getData();
        INDArray oldvalues = last_observation == null ? null : last_observation.getData();

        return !(world_state.getObservations().isEmpty() || world_state.getRewards().isEmpty()
                        || oldvalues.eq(newvalues).all());
    }

}
