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

package org.deeplearning4j.text.tokenization.tokenizer;

import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.NGramTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author sonali
 */
public class NGramTokenizerTest extends BaseDL4JTest {

    @Test
    public void testNGramTokenizer() throws Exception {
        String toTokenize = "Mary had a little lamb.";
        TokenizerFactory factory = new NGramTokenizerFactory(new DefaultTokenizerFactory(), 1, 2);
        Tokenizer tokenizer = factory.create(toTokenize);
        Tokenizer tokenizer2 = factory.create(toTokenize);
        while (tokenizer.hasMoreTokens()) {
            assertEquals(tokenizer.nextToken(), tokenizer2.nextToken());
        }

        int stringCount = factory.create(toTokenize).countTokens();
        List<String> tokens = factory.create(toTokenize).getTokens();
        assertEquals(9, stringCount);

        assertTrue(tokens.contains("Mary"));
        assertTrue(tokens.contains("had"));
        assertTrue(tokens.contains("a"));
        assertTrue(tokens.contains("little"));
        assertTrue(tokens.contains("lamb."));
        assertTrue(tokens.contains("Mary had"));
        assertTrue(tokens.contains("had a"));
        assertTrue(tokens.contains("a little"));
        assertTrue(tokens.contains("little lamb."));

        factory = new NGramTokenizerFactory(new DefaultTokenizerFactory(), 2, 2);
        tokens = factory.create(toTokenize).getTokens();
        assertEquals(4, tokens.size());

        assertTrue(tokens.contains("Mary had"));
        assertTrue(tokens.contains("had a"));
        assertTrue(tokens.contains("a little"));
        assertTrue(tokens.contains("little lamb."));
    }
}
