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

package org.deeplearning4j.optimizer.listener;

import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.optimize.listeners.CollectScoresIterationListener;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import static org.junit.Assert.*;

public class ScoreStatTest extends BaseDL4JTest {
    @Test
    public void testScoreStatSmall() {
        CollectScoresIterationListener.ScoreStat statTest = new CollectScoresIterationListener.ScoreStat();
        for (int i = 0; i < CollectScoresIterationListener.ScoreStat.BUCKET_LENGTH; ++i) {
            double score = (double)i;
            statTest.addScore(i, score);
        }

        List<long[]> indexes = statTest.getIndexes();
        List<double[]> scores = statTest.getScores();

        assertTrue(indexes.size() == 1);
        assertTrue(scores.size() == 1);

        assertTrue(indexes.get(0).length == CollectScoresIterationListener.ScoreStat.BUCKET_LENGTH);
        assertTrue(scores.get(0).length == CollectScoresIterationListener.ScoreStat.BUCKET_LENGTH);
        assertEquals(indexes.get(0)[indexes.get(0).length-1], CollectScoresIterationListener.ScoreStat.BUCKET_LENGTH-1);
        assertEquals(scores.get(0)[scores.get(0).length-1], CollectScoresIterationListener.ScoreStat.BUCKET_LENGTH-1, 1e-4);
    }

    @Test
    public void testScoreStatAverage() {
        int dataSize = 1000000;
        long[] indexes = new long[dataSize];
        double[] scores = new double[dataSize];

        for (int i = 0; i < dataSize; ++i) {
            indexes[i] = i;
            scores[i] = i;
        }

        CollectScoresIterationListener.ScoreStat statTest = new CollectScoresIterationListener.ScoreStat();
        for (int i = 0; i < dataSize; ++i) {
            statTest.addScore(indexes[i], scores[i]);
        }

        long[] indexesStored = statTest.getIndexes().get(0);
        double[] scoresStored = statTest.getScores().get(0);

        assertArrayEquals(indexes, indexesStored);
        assertArrayEquals(scores, scoresStored, 1e-4);
    }

    @Test
    public void testScoresClean() {
        int dataSize = 10256;  // expected to be placed in 2 buckets of 10k elements size
        long[] indexes = new long[dataSize];
        double[] scores = new double[dataSize];

        for (int i = 0; i < dataSize; ++i) {
            indexes[i] = i;
            scores[i] = i;
        }

        CollectScoresIterationListener.ScoreStat statTest = new CollectScoresIterationListener.ScoreStat();
        for (int i = 0; i < dataSize; ++i) {
            statTest.addScore(indexes[i], scores[i]);
        }

        long[] indexesEffective = statTest.getEffectiveIndexes();
        double[] scoresEffective = statTest.getEffectiveScores();

        assertArrayEquals(indexes, indexesEffective);
        assertArrayEquals(scores, scoresEffective, 1e-4);
    }

    @Ignore
    @Test
    public void testScoreStatBig() {
        CollectScoresIterationListener.ScoreStat statTest = new CollectScoresIterationListener.ScoreStat();
        long bigLength = (long)Integer.MAX_VALUE + 5;
        for (long i = 0; i < bigLength; ++i) {
            double score = (double)i;
            statTest.addScore(i, score);
        }

        List<long[]> indexes = statTest.getIndexes();
        List<double[]> scores = statTest.getScores();

        assertTrue(indexes.size() == 2);
        assertTrue(scores.size() == 2);

        for (int i = 0; i < 5; ++i) {
            assertTrue(indexes.get(1)[i] == Integer.MAX_VALUE + i);
            assertTrue(scores.get(1)[i] == Integer.MAX_VALUE + i);

        }
    }
}
