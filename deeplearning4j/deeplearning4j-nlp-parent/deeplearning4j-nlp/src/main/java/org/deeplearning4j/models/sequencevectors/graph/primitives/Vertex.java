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

package org.deeplearning4j.models.sequencevectors.graph.primitives;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;

/** Vertex in a graph
 *
 * @param <T> the type of the value/object associated with the vertex
 */
@AllArgsConstructor
public class Vertex<T extends SequenceElement> {

    private final int idx;
    @Getter
    @Setter
    private T value;

    public int vertexID() {
        return idx;
    }


    @Override
    public String toString() {
        return "vertex(" + idx + "," + (value != null ? value : "") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vertex))
            return false;
        Vertex<?> v = (Vertex<?>) o;
        if (idx != v.idx)
            return false;
        if ((value == null && v.value != null) || (value != null && v.value == null))
            return false;
        return value == null || value.equals(v.value);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + idx;
        result = 31 * result + (value == null ? 0 : value.hashCode());
        return result;
    }
}
