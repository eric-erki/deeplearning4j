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

import com.atilika.kuromoji.dict.ConnectionCosts;
import com.atilika.kuromoji.io.ByteBufferIO;
import org.deeplearning4j.BaseDL4JTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class ConnectionCostsCompilerTest extends BaseDL4JTest {

    private static ConnectionCosts connectionCosts;

    @BeforeClass
    public static void setUp() throws IOException {
        File costsFile = File.createTempFile("kuromoji-connectioncosts-", ".bin");
        costsFile.deleteOnExit();

        String costs = "" + "3 3\n" + "0 0 1\n" + "0 1 2\n" + "0 2 3\n" + "1 0 4\n" + "1 1 5\n" + "1 2 6\n" + "2 0 7\n"
                        + "2 1 8\n" + "2 2 9\n";

        ConnectionCostsCompiler compiler = new ConnectionCostsCompiler(new FileOutputStream(costsFile));

        compiler.readCosts(new ByteArrayInputStream(costs.getBytes(StandardCharsets.UTF_8)));

        compiler.compile();

        DataInputStream dataInput = new DataInputStream(new FileInputStream(costsFile));

        int size = dataInput.readInt();
        ShortBuffer costsBuffer = ByteBufferIO.read(dataInput).asShortBuffer();
        dataInput.close();

        connectionCosts = new ConnectionCosts(size, costsBuffer);
    }

    @Test
    public void testCosts() {
        int cost = 1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(cost++, connectionCosts.get(i, j));
            }
        }
    }
}
