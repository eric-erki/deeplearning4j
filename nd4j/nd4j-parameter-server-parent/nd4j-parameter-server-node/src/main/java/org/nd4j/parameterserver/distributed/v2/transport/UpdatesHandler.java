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

package org.nd4j.parameterserver.distributed.v2.transport;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.reactivestreams.Subscriber;

/**
 * This interface describes Subscriber capable of providing safe access to underlying parameters
 */
public interface UpdatesHandler extends Subscriber<INDArray> {
    /**
     * This method returns parameters array maintained by this handler
     * @return
     */
    INDArray getParametersArray();
}
