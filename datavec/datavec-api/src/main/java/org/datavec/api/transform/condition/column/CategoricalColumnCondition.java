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

package org.datavec.api.transform.condition.column;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.datavec.api.transform.condition.ConditionOp;
import org.datavec.api.transform.condition.SequenceConditionMode;
import org.datavec.api.writable.Writable;
import org.nd4j.shade.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * Condition that applies to the values in a Categorical column, using a {@link ConditionOp}
 *
 * @author Alex Black
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CategoricalColumnCondition extends BaseColumnCondition {

    private final ConditionOp op;
    private final String value;
    private final Set<String> set;

    /**
     * Constructor for conditions equal or not equal.
     * Uses default sequence condition mode, {@link BaseColumnCondition#DEFAULT_SEQUENCE_CONDITION_MODE}
     *
     * @param columnName Column to check for the condition
     * @param op         Operation (== or != only)
     * @param value      Value to use in the condition
     */
    public CategoricalColumnCondition(String columnName, ConditionOp op, String value) {
        this(columnName, DEFAULT_SEQUENCE_CONDITION_MODE, op, value);
    }

    /**
     * Constructor for conditions equal or not equal
     *
     * @param columnName            Column to check for the condition
     * @param sequenceConditionMode Mode for handling sequence data
     * @param op                    Operation (== or != only)
     * @param value                 Value to use in the condition
     */
    public CategoricalColumnCondition(String columnName, SequenceConditionMode sequenceConditionMode, ConditionOp op,
                    String value) {
        super(columnName, sequenceConditionMode);
        if (op != ConditionOp.Equal && op != ConditionOp.NotEqual) {
            throw new IllegalArgumentException(
                            "Invalid condition op: can only use this constructor with Equal or NotEqual conditions");
        }
        this.op = op;
        this.value = value;
        this.set = null;
    }


    /**
     * Constructor for operations: ConditionOp.InSet, ConditionOp.NotInSet
     * Uses default sequence condition mode, {@link BaseColumnCondition#DEFAULT_SEQUENCE_CONDITION_MODE}
     *
     * @param columnName Column to check for the condition
     * @param op         Operation. Must be either ConditionOp.InSet, ConditionOp.NotInSet
     * @param set        Set to use in the condition
     */
    public CategoricalColumnCondition(String columnName, ConditionOp op, Set<String> set) {
        this(columnName, DEFAULT_SEQUENCE_CONDITION_MODE, op, set);
    }

    //Private constructor for Jackson deserialization only
    private CategoricalColumnCondition(@JsonProperty("columnName") String columnName,
                    @JsonProperty("op") ConditionOp op, @JsonProperty("value") String value,
                    @JsonProperty("set") Set<String> set) {
        super(columnName, DEFAULT_SEQUENCE_CONDITION_MODE);
        this.op = op;
        this.value = value;
        this.set = set;
    }

    /**
     * Constructor for operations: ConditionOp.InSet, ConditionOp.NotInSet
     *
     * @param columnName            Column to check for the condition
     * @param sequenceConditionMode Mode for handling sequence data
     * @param op                    Operation. Must be either ConditionOp.InSet, ConditionOp.NotInSet
     * @param set                   Set to use in the condition
     */
    public CategoricalColumnCondition(String columnName, SequenceConditionMode sequenceConditionMode, ConditionOp op,
                    Set<String> set) {
        super(columnName, sequenceConditionMode);
        if (op != ConditionOp.InSet && op != ConditionOp.NotInSet) {
            throw new IllegalArgumentException(
                            "Invalid condition op: can ONLY use this constructor with InSet or NotInSet ops");
        }
        this.op = op;
        this.value = null;
        this.set = set;
    }


    @Override
    public boolean columnCondition(Writable writable) {
        return op.apply(writable.toString(), value, set);
    }

    @Override
    public String toString() {
        return "CategoricalColumnCondition(columnName=\"" + columnName + "\"," + op + ","
                        + (op == ConditionOp.NotInSet || op == ConditionOp.InSet ? set : value) + ")";
    }

    /**
     * Condition on arbitrary input
     *
     * @param input the input to return
     *              the condition for
     * @return true if the condition is met
     * false otherwise
     */
    @Override
    public boolean condition(Object input) {
        return op.apply(input.toString(), value, set);
    }
}
