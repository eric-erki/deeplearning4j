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

import com.atilika.kuromoji.dict.CharacterDefinitions;
import com.atilika.kuromoji.dict.UnknownDictionary;
import com.atilika.kuromoji.io.IntegerArrayIO;
import com.atilika.kuromoji.io.StringArrayIO;
import org.deeplearning4j.BaseDL4JTest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class UnknownDictionaryCompilerTest extends BaseDL4JTest {

    private static UnknownDictionary unknownDictionary;

    private static CharacterDefinitions characterDefinitions;

    private static int[][] costs;

    private static int[][] references;

    private static String[][] features;

    @BeforeClass
    public static void setUp() throws IOException {
        File charDef = File.createTempFile("kuromoji-chardef-", ".bin");
        charDef.deleteOnExit();

        CharacterDefinitionsCompiler charDefCompiler =
                        new CharacterDefinitionsCompiler(new BufferedOutputStream(new FileOutputStream(charDef)));
        charDefCompiler.readCharacterDefinition(new BufferedInputStream(
                        CharacterDefinitionsCompilerTest.class.getClassLoader().getResourceAsStream("deeplearning4j-nlp-japanese/char.def")),
                        "euc-jp");
        charDefCompiler.compile();

        Map<String, Integer> categoryMap = charDefCompiler.makeCharacterCategoryMap();

        File unkDef = File.createTempFile("kuromoji-unkdef-", ".bin");
        unkDef.deleteOnExit();

        UnknownDictionaryCompiler unkDefCompiler =
                        new UnknownDictionaryCompiler(categoryMap, new FileOutputStream(unkDef));

        unkDefCompiler.readUnknownDefinition(new BufferedInputStream(
                        UnknownDictionaryCompilerTest.class.getClassLoader().getResourceAsStream("deeplearning4j-nlp-japanese/unk.def")), "euc-jp");

        unkDefCompiler.compile();

        InputStream charDefInput = new BufferedInputStream(new FileInputStream(charDef));

        int[][] definitions = IntegerArrayIO.readSparseArray2D(charDefInput);
        int[][] mappings = IntegerArrayIO.readSparseArray2D(charDefInput);
        String[] symbols = StringArrayIO.readArray(charDefInput);

        characterDefinitions = new CharacterDefinitions(definitions, mappings, symbols);

        InputStream unkDefInput = new BufferedInputStream(new FileInputStream(unkDef));

        costs = IntegerArrayIO.readArray2D(unkDefInput);
        references = IntegerArrayIO.readArray2D(unkDefInput);
        features = StringArrayIO.readArray2D(unkDefInput);

        unknownDictionary = new UnknownDictionary(characterDefinitions, references, costs, features);

    }

    @Test
    public void testCostsAndFeatures() {
        int[] categories = characterDefinitions.lookupCategories('???');

        // KANJI & KANJINUMERIC
        assertEquals(2, categories.length);

        assertArrayEquals(new int[] {5, 6}, categories);

        // KANJI entries
        assertArrayEquals(new int[] {2, 3, 4, 5, 6, 7}, unknownDictionary.lookupWordIds(categories[0]));

        // KANJI feature variety
        assertArrayEquals(new String[] {"??????", "??????", "*", "*", "*", "*", "*"}, unknownDictionary.getAllFeaturesArray(2));

        assertArrayEquals(new String[] {"??????", "????????????", "*", "*", "*", "*", "*"},
                        unknownDictionary.getAllFeaturesArray(3));

        assertArrayEquals(new String[] {"??????", "????????????", "??????", "??????", "*", "*", "*"},
                        unknownDictionary.getAllFeaturesArray(4));

        assertArrayEquals(new String[] {"??????", "????????????", "??????", "*", "*", "*", "*"},
                        unknownDictionary.getAllFeaturesArray(5));

        assertArrayEquals(new String[] {"??????", "????????????", "??????", "??????", "*", "*", "*"},
                        unknownDictionary.getAllFeaturesArray(6));

        assertArrayEquals(new String[] {"??????", "????????????", "??????", "??????", "*", "*", "*"},
                        unknownDictionary.getAllFeaturesArray(6));

        // KANJINUMERIC entry
        assertArrayEquals(new int[] {29}, unknownDictionary.lookupWordIds(categories[1]));

        // KANJINUMERIC costs
        assertEquals(1295, unknownDictionary.getLeftId(29));
        assertEquals(1295, unknownDictionary.getRightId(29));
        assertEquals(27473, unknownDictionary.getWordCost(29));

        // KANJINUMERIC features
        assertArrayEquals(new String[] {"??????", "???", "*", "*", "*", "*", "*"}, unknownDictionary.getAllFeaturesArray(29));
    }
}
