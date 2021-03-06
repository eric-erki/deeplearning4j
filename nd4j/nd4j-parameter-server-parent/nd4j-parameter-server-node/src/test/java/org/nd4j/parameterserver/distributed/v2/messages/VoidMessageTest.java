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

package org.nd4j.parameterserver.distributed.v2.messages;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.nd4j.common.tests.BaseND4JTest;
import org.nd4j.common.util.SerializationUtils;
import org.nd4j.parameterserver.distributed.v2.messages.pairs.handshake.HandshakeRequest;

import static org.junit.Assert.*;

@Slf4j
public class VoidMessageTest extends BaseND4JTest {
    @Test
    public void testHandshakeSerialization_1() throws Exception {
        val req = new HandshakeRequest();
        req.setOriginatorId("1234");

        val bytes = SerializationUtils.toByteArray(req);

        VoidMessage res = SerializationUtils.deserialize(bytes);

        assertEquals(req.getOriginatorId(), res.getOriginatorId());
    }

    @Test
    public void testHandshakeSerialization_2() throws Exception {
        val req = new HandshakeRequest();
        req.setOriginatorId("1234");

        val bytes = SerializationUtils.toByteArray(req);

        VoidMessage res = VoidMessage.fromBytes(bytes);

        assertEquals(req.getOriginatorId(), res.getOriginatorId());
    }
}