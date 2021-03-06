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
 * ND4JOpProfilerException: Thrown by the op profiler (if enabled) for example on NaN panic
 *
 * @author Alex Black
 */
public class ND4JOpProfilerException extends ND4JIllegalStateException {
    public ND4JOpProfilerException() {
    }
    public ND4JOpProfilerException(String message) {
        super(message);
    }

    public ND4JOpProfilerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ND4JOpProfilerException(Throwable cause) {
        super(cause);
    }
}
