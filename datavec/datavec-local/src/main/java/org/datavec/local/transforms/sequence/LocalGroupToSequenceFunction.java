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

package org.datavec.local.transforms.sequence;

import lombok.AllArgsConstructor;
import org.datavec.api.transform.sequence.SequenceComparator;
import org.datavec.api.writable.Writable;
import org.nd4j.common.function.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Function for grouping independent values/examples into a sequence, and then sorting them
 * using a provided {@link SequenceComparator}
 *
 * @author Alex Black
 */
@AllArgsConstructor
public class LocalGroupToSequenceFunction implements Function<List<List<Writable>>, List<List<Writable>>> {

    private final SequenceComparator comparator;

    @Override
    public List<List<Writable>> apply(List<List<Writable>> lists) {

        List<List<Writable>> list = new ArrayList<>();
        list.addAll(lists);

        Collections.sort(list, comparator);

        return list;
    }
}
