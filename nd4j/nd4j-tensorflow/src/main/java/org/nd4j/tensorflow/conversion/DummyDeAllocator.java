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

package org.nd4j.tensorflow.conversion;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.tensorflow.Deallocator_Pointer_long_Pointer;

public class DummyDeAllocator extends Deallocator_Pointer_long_Pointer {
    private static DummyDeAllocator INSTANCE = new DummyDeAllocator();

    public static DummyDeAllocator getInstance() {
        return INSTANCE;
    }

    @Override
    public void call(Pointer pointer, long l, Pointer pointer1) {
    }
}
