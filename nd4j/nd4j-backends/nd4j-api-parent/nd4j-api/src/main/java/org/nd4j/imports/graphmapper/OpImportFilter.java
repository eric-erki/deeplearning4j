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

package org.nd4j.imports.graphmapper;

import org.nd4j.autodiff.samediff.SameDiff;

import java.util.Map;

/**
 * Define whether the operation should be skipped during import
 */
public interface OpImportFilter<GRAPH_TYPE, NODE_TYPE, ATTR_TYPE> {

    /**
     * If true: the op should be skipped for import, and its output variables should not be created. If false: the op should be imported
     * @param nodeDef           Node
     * @param initWith          SameDiff instance
     * @param attributesForNode Attributes for the node
     * @param graph             Graph to import from
     * @return True if the op should be skipped during import
     */
    boolean skipOp(NODE_TYPE nodeDef, SameDiff initWith, Map<String,ATTR_TYPE> attributesForNode, GRAPH_TYPE graph);

}
