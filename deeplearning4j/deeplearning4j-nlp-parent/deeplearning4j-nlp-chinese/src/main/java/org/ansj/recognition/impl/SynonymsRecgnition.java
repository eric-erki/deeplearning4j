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

package org.ansj.recognition.impl;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.SynonymsLibrary;
import org.ansj.recognition.Recognition;
import org.nlpcn.commons.lang.tire.domain.SmartForest;

import java.util.List;

/**
 * 同义词功能
 * 
 * @author Ansj
 *
 */
public class SynonymsRecgnition implements Recognition {

    private static final long serialVersionUID = 5961499108093950130L;

    private SmartForest<List<String>> synonyms = null;

    public SynonymsRecgnition() {
        this.synonyms = SynonymsLibrary.get();
    }

    public SynonymsRecgnition(String key) {
        this.synonyms = SynonymsLibrary.get(key);
    }

    public SynonymsRecgnition(SmartForest<List<String>> synonyms) {
        this.synonyms = synonyms;
    }

    @Override
    public void recognition(Result result) {
        for (Term term : result) {
            SmartForest<List<String>> branch = synonyms.getBranch(term.getName());
            if (branch != null && branch.getStatus() > 1) {
                List<String> syns = branch.getParam();
                if (syns != null) {
                    term.setSynonyms(syns);
                }
            }
        }
    }

}
