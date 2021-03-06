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

package org.nd4j.parameterserver.distributed.messages;

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.common.tests.BaseND4JTest;
import org.nd4j.parameterserver.distributed.conf.VoidConfiguration;
import org.nd4j.parameterserver.distributed.enums.NodeRole;
import org.nd4j.parameterserver.distributed.logic.completion.Clipboard;
import org.nd4j.parameterserver.distributed.logic.Storage;
import org.nd4j.parameterserver.distributed.messages.requests.SkipGramRequestMessage;
import org.nd4j.parameterserver.distributed.training.TrainingDriver;
import org.nd4j.parameterserver.distributed.transport.Transport;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * @author raver119@gmail.com
 */
@Ignore
@Deprecated
public class FrameTest extends BaseND4JTest {
    @Before
    public void setUp() throws Exception {

    }

    /**
     * Simple test for Frame functionality
     */
    @Test(timeout = 30000L)
    public void testFrame1() {
        final AtomicInteger count = new AtomicInteger(0);

        Frame<TrainingMessage> frame = new Frame<>();

        for (int i = 0; i < 10; i++) {
            frame.stackMessage(new TrainingMessage() {
                @Override
                public byte getCounter() {
                    return 2;
                }

                @Override
                public void setTargetId(short id) {

                }

                @Override
                public int getRetransmitCount() {
                    return 0;
                }

                @Override
                public void incrementRetransmitCount() {

                }

                @Override
                public long getFrameId() {
                    return 0;
                }

                @Override
                public void setFrameId(long frameId) {

                }

                @Override
                public long getOriginatorId() {
                    return 0;
                }

                @Override
                public void setOriginatorId(long id) {

                }

                @Override
                public short getTargetId() {
                    return 0;
                }

                @Override
                public long getTaskId() {
                    return 0;
                }

                @Override
                public int getMessageType() {
                    return 0;
                }

                @Override
                public byte[] asBytes() {
                    return new byte[0];
                }

                @Override
                public UnsafeBuffer asUnsafeBuffer() {
                    return null;
                }

                @Override
                public void attachContext(VoidConfiguration voidConfiguration,
                                TrainingDriver<? extends TrainingMessage> trainer, Clipboard clipboard,
                                Transport transport, Storage storage, NodeRole role, short shardIndex) {
                    // no-op intentionally
                }

                @Override
                public void extractContext(BaseVoidMessage message) {
                    // no-op intentionally
                }

                @Override
                public void processMessage() {
                    count.incrementAndGet();
                }

                @Override
                public boolean isJoinSupported() {
                    return false;
                }

                @Override
                public void joinMessage(VoidMessage message) {
                    // no-op
                }

                @Override
                public boolean isBlockingMessage() {
                    return false;
                }
            });
        }

        assertEquals(10, frame.size());

        frame.processMessage();

        assertEquals(20, count.get());
    }


    @Test(timeout = 30000L)
    public void testJoin1() throws Exception {
        SkipGramRequestMessage sgrm = new SkipGramRequestMessage(0, 1, new int[] {3, 4, 5}, new byte[] {0, 1, 0},
                        (short) 0, 0.01, 119L);
        Frame<SkipGramRequestMessage> frame = new Frame<>(sgrm);
        for (int i = 0; i < 10; i++) {
            frame.stackMessage(sgrm);
        }

        // all messages should be stacked into one message
        assertEquals(1, frame.size());
        assertEquals(11, sgrm.getCounter());
    }
}
