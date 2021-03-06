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

package org.datavec.api.transform.serde;

import org.nd4j.shade.jackson.databind.ObjectMapper;

/**
 * Serializer used for converting objects (Transforms, Conditions, etc) to JSON format
 *
 * @author Alex Black
 */
public class JsonSerializer extends BaseSerializer {

    private ObjectMapper om;

    public JsonSerializer() {
        this.om = JsonMappers.getMapper();
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return om;
    }
}
