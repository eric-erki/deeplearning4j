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

import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nlp.uima.sentenceiterator.UimaResultSetIterator;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Brad Heap nzv8fan@gmail.com
 */
public class UimaResultSetIteratorTest extends BaseDL4JTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testSingleSentenceRow() throws Exception {

        // Setup a mock ResultSet object
        ResultSet resultSetMock = mock(ResultSet.class);

        // when .next() is called, first time true, then false
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("line")).thenReturn("The quick brown fox.");

        UimaResultSetIterator iterator = new UimaResultSetIterator(resultSetMock, "line");

        int cnt = 0;
        while (iterator.hasNext()) {
            String line = iterator.nextSentence();
            cnt++;
        }

        assertEquals(1, cnt);

    }

    @Test
    public void testMultipleSentenceRow() throws Exception {

        // Setup a mock ResultSet object
        ResultSet resultSetMock = mock(ResultSet.class);

        // when .next() is called, first time true, then false
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("line")).thenReturn("The quick brown fox. The lazy dog. Over a fence.");

        UimaResultSetIterator iterator = new UimaResultSetIterator(resultSetMock, "line");

        int cnt = 0;
        while (iterator.hasNext()) {
            String line = iterator.nextSentence();
            cnt++;
        }

        assertEquals(3, cnt);

    }

    @Test
    public void testMultipleSentencesAndMultipleRows() throws Exception {

        // Setup a mock ResultSet object
        ResultSet resultSetMock = mock(ResultSet.class);

        // when .next() is called, first time true, then false
        when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSetMock.getString("line")).thenReturn("The quick brown fox.")
                        .thenReturn("The lazy dog. Over a fence.");

        UimaResultSetIterator iterator = new UimaResultSetIterator(resultSetMock, "line");

        int cnt = 0;
        while (iterator.hasNext()) {
            String line = iterator.nextSentence();
            cnt++;
        }

        assertEquals(3, cnt);

    }

    @Test
    public void testMultipleSentencesAndMultipleRowsAndReset() throws Exception {

        // Setup a mock ResultSet object
        ResultSet resultSetMock = mock(ResultSet.class);

        // when .next() is called, first time true, then false
        when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(true)
                        .thenReturn(false);
        when(resultSetMock.getString("line")).thenReturn("The quick brown fox.")
                        .thenReturn("The lazy dog. Over a fence.").thenReturn("The quick brown fox.")
                        .thenReturn("The lazy dog. Over a fence.");

        UimaResultSetIterator iterator = new UimaResultSetIterator(resultSetMock, "line");

        int cnt = 0;
        while (iterator.hasNext()) {
            String line = iterator.nextSentence();
            cnt++;
        }

        assertEquals(3, cnt);

        iterator.reset();

        cnt = 0;
        while (iterator.hasNext()) {
            String line = iterator.nextSentence();
            cnt++;
        }

        assertEquals(3, cnt);
    }

}
