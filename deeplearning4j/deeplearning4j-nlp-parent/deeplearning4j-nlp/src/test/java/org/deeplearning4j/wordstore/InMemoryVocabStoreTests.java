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

package org.deeplearning4j.wordstore;

import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created by agibsonccc on 8/31/14.
 */
public class InMemoryVocabStoreTests extends BaseDL4JTest {

    @Test
    public void testStorePut() {
        VocabCache<VocabWord> cache = new InMemoryLookupCache();
        assertFalse(cache.containsWord("hello"));
        cache.addWordToIndex(0, "hello");
        assertTrue(cache.containsWord("hello"));
        assertEquals(1, cache.numWords());
        assertEquals("hello", cache.wordAtIndex(0));
    }
}
