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

package org.nd4j.parameterserver.updater.storage;

import org.junit.Test;
import org.nd4j.common.tests.BaseND4JTest;
import org.nd4j.aeron.ipc.NDArrayMessage;
import org.nd4j.linalg.factory.Nd4j;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by agibsonccc on 12/2/16.
 */
public class UpdaterStorageTests extends BaseND4JTest {


    @Test(expected = UnsupportedOperationException.class)
    public void testNone() {
        UpdateStorage updateStorage = new NoUpdateStorage();
        NDArrayMessage message = NDArrayMessage.wholeArrayUpdate(Nd4j.scalar(1.0));
        updateStorage.addUpdate(message);
        assertEquals(1, updateStorage.numUpdates());
        assertEquals(message, updateStorage.getUpdate(0));
        updateStorage.close();
    }

    @Test(timeout = 30000L)
    public void testInMemory() {
        UpdateStorage updateStorage = new InMemoryUpdateStorage();
        NDArrayMessage message = NDArrayMessage.wholeArrayUpdate(Nd4j.scalar(1.0));
        updateStorage.addUpdate(message);
        assertEquals(1, updateStorage.numUpdates());
        assertEquals(message, updateStorage.getUpdate(0));
        updateStorage.clear();
        assertEquals(0, updateStorage.numUpdates());
        updateStorage.close();
    }

}
