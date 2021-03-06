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

package org.deeplearning4j.ui.model.storage.impl;

import lombok.AllArgsConstructor;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.core.storage.StatsStorageEvent;
import org.deeplearning4j.core.storage.StatsStorageListener;
import org.nd4j.common.primitives.Pair;

import java.util.Queue;

/**
 * A very simple {@link StatsStorageListener}, that adds the {@link StatsStorageEvent} instances and the specified
 * {@link StatsStorage} instance (i.e., the source) to the specified queue for later processing.
 *
 * @author Alex Black
 */
@AllArgsConstructor
public class QueuePairStatsStorageListener implements StatsStorageListener {

    private final StatsStorage statsStorage;
    private final Queue<Pair<StatsStorage, StatsStorageEvent>> queue;

    @Override
    public void notify(StatsStorageEvent event) {
        queue.add(new Pair<>(statsStorage, event));
    }
}
