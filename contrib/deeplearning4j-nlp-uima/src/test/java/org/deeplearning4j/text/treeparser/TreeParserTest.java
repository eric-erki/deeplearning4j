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

package org.deeplearning4j.text.treeparser;

import org.cleartk.syntax.constituent.type.TreebankNode;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.layers.feedforward.autoencoder.recursive.Tree;
import org.deeplearning4j.nlp.uima.corpora.treeparser.TreeParser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Basic Tree parser tests
 * @author  Adam Gibson
 */
public class TreeParserTest extends BaseDL4JTest {
    private TreeParser parser;

    @Before
    public void init() throws Exception {
        parser = new TreeParser();
    }


    @Test
    public void testNumTrees() throws Exception {
        List<Tree> trees = parser.getTrees("This is one sentence. This is another sentence.");
        assertEquals(2, trees.size());

    }


    @Test
    public void testHierarchy() throws Exception {
        List<Tree> trees = parser.getTrees("This is one sentence. This is another sentence.");
        List<TreebankNode> treebankTrees = parser.getTreebankTrees("This is one sentence. This is another sentence.");
        assertEquals(treebankTrees.size(), trees.size());

        for (int i = 0; i < treebankTrees.size(); i++) {
            Tree t = trees.get(i);
            TreebankNode t2 = treebankTrees.get(i);
            assertEquals(t.children().size(), t2.getChildren().size());
        }

    }

}
