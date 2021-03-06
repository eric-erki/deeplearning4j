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

package org.nd4j.common.primitives;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Simple triple elements holder implementation
 * @author raver119@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Triple<F, S, T> implements Serializable {
    private static final long serialVersionUID = 119L;

    protected F first;
    protected S second;
    protected T third;


    public F getLeft() {
        return first;
    }

    public S getMiddle() {
        return second;
    }

    public T getRight() {
        return third;
    }

    public static <F, S, T> Triple<F, S,T> tripleOf(F first, S second, T third) {
        return new Triple<>(first, second, third);
    }

    public static <F, S, T> Triple<F, S,T> of(F first, S second, T third) {
        return new Triple<>(first, second, third);
    }
}
