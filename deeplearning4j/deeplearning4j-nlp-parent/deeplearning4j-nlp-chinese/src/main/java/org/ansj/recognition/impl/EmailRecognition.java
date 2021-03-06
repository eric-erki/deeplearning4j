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
import org.ansj.recognition.Recognition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 电子邮箱抽取
 * 
 * @author ansj
 *
 */
public class EmailRecognition implements Recognition {

    private static Map<String, String> FEATURE = new HashMap<>();

    private static final String NOT_HEAD = "NOT";
    private static final String NATURE_HEAD = "nature:";
    private static final String ALL = "ALL";

    static {
        FEATURE.put("-", NOT_HEAD);
        FEATURE.put("_", NOT_HEAD);
        FEATURE.put(".", NOT_HEAD);
        FEATURE.put(NATURE_HEAD + "en", ALL);
        FEATURE.put(NATURE_HEAD + "m", ALL);

    }

    @Override
    public void recognition(Result result) {

        List<Term> terms = result.getTerms();

        for (Term term : terms) {
            if (!"@".equals(term.getName())) {
                continue;
            }

        }

        for (Iterator<Term> iterator = terms.iterator(); iterator.hasNext();) {
            Term term = iterator.next();
            if (term.getName() == null) {
                iterator.remove();
            }
        }

    }
}
