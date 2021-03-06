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

import com.atilika.kuromoji.buffer.WordIdMap;
import org.deeplearning4j.BaseDL4JTest;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class WordIdMapCompilerTest extends BaseDL4JTest {

    @Test
    public void testGrowableArray() {
        WordIdMapCompiler.GrowableIntArray array = new WordIdMapCompiler.GrowableIntArray(5);
        array.set(3, 1);
        assertEquals("[0, 0, 0, 1]", Arrays.toString(array.getArray()));
        array.set(0, 2);
        array.set(10, 3);
        assertEquals("[2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 3]", Arrays.toString(array.getArray()));
    }

    @Test
    public void testCompiler() throws IOException {
        WordIdMapCompiler compiler = new WordIdMapCompiler();
        compiler.addMapping(3, 1);
        compiler.addMapping(3, 2);
        compiler.addMapping(3, 3);
        compiler.addMapping(10, 0);

        File file = File.createTempFile("kuromoji-wordid-", ".bin");
        file.deleteOnExit();

        OutputStream output = new BufferedOutputStream(new FileOutputStream(file));

        compiler.write(output);
        output.close();

        InputStream input = new BufferedInputStream(new FileInputStream(file));

        WordIdMap wordIds = new WordIdMap(input);

        assertEquals("[1, 2, 3]", Arrays.toString(wordIds.lookUp(3)));
        assertEquals("[0]", Arrays.toString(wordIds.lookUp(10)));
        assertEquals("[]", Arrays.toString(wordIds.lookUp(1)));
    }
}
