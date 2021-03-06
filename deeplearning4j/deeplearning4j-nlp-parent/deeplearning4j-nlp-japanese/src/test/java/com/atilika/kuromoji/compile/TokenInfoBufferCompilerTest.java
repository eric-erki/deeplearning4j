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

package com.atilika.kuromoji.compile;

import com.atilika.kuromoji.buffer.BufferEntry;
import com.atilika.kuromoji.buffer.TokenInfoBuffer;
import org.deeplearning4j.BaseDL4JTest;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TokenInfoBufferCompilerTest extends BaseDL4JTest {

    @Test
    public void testReadAndWriteFromBuffer() throws Exception {
        List<Short> shorts = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            shorts.add((short) i);
        }

        ByteBuffer buffer = ByteBuffer.allocate(shorts.size() * 2 + 2);

        buffer.putShort((short) shorts.size());

        for (Short s : shorts) {
            buffer.putShort(s);
        }

        buffer.position(0);

        short count = buffer.getShort();

        List<Short> readShorts = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            readShorts.add(buffer.getShort());
        }

        for (int i = 0; i < shorts.size(); i++) {
            assertEquals(readShorts.get(i), shorts.get(i));
        }
    }

    @Test
    public void testReadAndLookUpTokenInfo() throws Exception {
        List<Short> tokenInfo = new ArrayList<>();
        List<Integer> features = new ArrayList<>();

        short[] tokenInfos = new short[3];
        tokenInfos[0] = 1;
        tokenInfos[1] = 2;
        tokenInfos[2] = 3;

        int[] featureInfos = new int[2];
        featureInfos[0] = 73;
        featureInfos[1] = 99;

        tokenInfo.add((short) 1);
        tokenInfo.add((short) 2);
        tokenInfo.add((short) 3);

        features.add(73);
        features.add(99);

        BufferEntry entry = new BufferEntry();
        entry.tokenInfo = tokenInfo;
        entry.features = features;

        entry.tokenInfos = tokenInfos;
        entry.featureInfos = featureInfos;

        List<BufferEntry> bufferEntries = new ArrayList<>();
        bufferEntries.add(entry);

        File file = File.createTempFile("kuromoji-tokeinfo-buffer-", ".bin");
        file.deleteOnExit();

        TokenInfoBufferCompiler compiler = new TokenInfoBufferCompiler(new FileOutputStream(file), bufferEntries);

        compiler.compile();

        TokenInfoBuffer tokenInfoBuffer2 = new TokenInfoBuffer(new FileInputStream(file));

        assertEquals(99, tokenInfoBuffer2.lookupFeature(0, 1));
        assertEquals(73, tokenInfoBuffer2.lookupFeature(0, 0));
    }

    @Test
    public void testCompleteLookUp() throws Exception {
        Map<Integer, String> resultMap = new HashMap<>();

        resultMap.put(73, "hello");
        resultMap.put(42, "?????????");
        resultMap.put(99, "???????????????");

        List<Short> tokenInfo = new ArrayList<>();
        List<Integer> features = new ArrayList<>();

        tokenInfo.add((short) 1);
        tokenInfo.add((short) 2);
        tokenInfo.add((short) 3);

        features.add(73);
        features.add(99);

        BufferEntry entry = new BufferEntry();
        entry.tokenInfo = tokenInfo;
        entry.features = features;

        List<BufferEntry> bufferEntries = new ArrayList<>();
        bufferEntries.add(entry);

        File file = File.createTempFile("kuromoji-tokeinfo-buffer-", ".bin");
        file.deleteOnExit();

        TokenInfoBufferCompiler compiler = new TokenInfoBufferCompiler(new FileOutputStream(file), bufferEntries);

        compiler.compile();

        TokenInfoBuffer tokenInfoBuffer2 = new TokenInfoBuffer(new FileInputStream(file));

        BufferEntry result = tokenInfoBuffer2.lookupEntry(0);

        assertEquals("hello", resultMap.get(result.featureInfos[0]));
        assertEquals("???????????????", resultMap.get(result.featureInfos[1]));
    }
}
