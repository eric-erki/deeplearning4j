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

package org.deeplearning4j.text.sentenceiterator;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nlp.uima.sentenceiterator.UimaSentenceIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by agibsonccc on 9/9/14.
 */
public class SentenceIteratorTest extends BaseDL4JTest {

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();
    
    public File testTxt;
    public File testSingle;
    public File testMulti;

    @Before
    public void before() throws Exception {
        testSingle = testDir.newFolder();
        testTxt = new File(testSingle, "test.txt");
        FileUtils.writeLines(testTxt, Arrays.asList("Hello", "My", "Name"));


        testMulti = testDir.newFolder();
        for (int i = 0; i < 2; i++) {
            File newTestFile = new File(testMulti, "testfile-" + i);
            FileUtils.writeLines(newTestFile, Arrays.asList("Sentence 1.", "Sentence 2.", "Sentence 3."));

        }

    }


    @Test
    public void testUimaSentenceIterator() throws Exception {
        SentenceIterator multiIter = UimaSentenceIterator.createWithPath(testMulti.getAbsolutePath());
        SentenceIterator iter = UimaSentenceIterator.createWithPath(testSingle.getAbsolutePath());
        testMulti(multiIter, 1);
        testMulti(iter, 1);

    }

    @Test
    public void testFileSentenceIterator() throws Exception {
        SentenceIterator iter = new FileSentenceIterator(testSingle);
        SentenceIterator multiIter = new FileSentenceIterator(testMulti);
        testSingle(iter);
        testMulti(multiIter, 3);

    }



    public void testSingle(SentenceIterator iter) {
        assertTrue(iter.hasNext());

        String sentence = iter.nextSentence();
        assertTrue(iter.hasNext());
        assertEquals("Hello", sentence);
        assertEquals("My", iter.nextSentence());
        assertEquals("Name", iter.nextSentence());
        assertFalse(iter.hasNext());

    }

    public void testMulti(SentenceIterator iter, int expectedSentences) {
        assertTrue(iter.hasNext());
        for (int i = 0; i < expectedSentences * 2; i++) {
            iter.nextSentence();
        }

        assertFalse(iter.hasNext());

    }

    @After
    public void after() throws Exception {
        File test = testSingle;
        test.mkdir();
        FileUtils.deleteQuietly(test);
        FileUtils.deleteQuietly(testMulti);
    }



}
