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

package org.nd4j.linalg.exception;

/**
 *  ND4JIllegalAccessException is thrown on illegal access cases, i.e. bad concurrent access on object that doesn't support that
 *
 * @author raver119@protonmail.com
 */
public class ND4JIllegalAccessException extends ND4JException {

    public ND4JIllegalAccessException() {}

    public ND4JIllegalAccessException(String message) {
        super(message);
    }

    public ND4JIllegalAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ND4JIllegalAccessException(Throwable cause) {
        super(cause);
    }
}
