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

package org.nd4j.linalg.api.memory.stash;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author raver119@gmail.com
 */
public abstract class BasicStash<T extends Object> implements Stash<T> {
    protected Map<T, INDArray> stash = new ConcurrentHashMap<>();

    protected BasicStash() {

    }

    @Override
    public boolean checkIfExists(T key) {
        /*
            Just checkin'
         */
        return false;
    }

    @Override
    public void put(T key, INDArray object) {
        /*
            Basically we want to get DataBuffer here, and store it here together with shape
            Special case here is GPU: we want to synchronize HOST memory, and store only HOST memory.
         */
    }

    @Override
    public INDArray get(T key) {
        /*
            We want to restore INDArray here, In case of GPU backend - we want to ensure data is replicated to device.
         */
        return null;
    }

    @Override
    public void purge() {
        /*
            We want to purge all stored stuff here.
         */
    }
}
