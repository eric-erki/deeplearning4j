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

package org.datavec.api.transform.transform.categorical;

import lombok.Data;
import org.datavec.api.transform.metadata.CategoricalMetaData;
import org.datavec.api.transform.metadata.ColumnMetaData;
import org.datavec.api.transform.metadata.IntegerMetaData;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.BaseTransform;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Writable;
import org.nd4j.shade.jackson.annotation.JsonIgnoreProperties;
import org.nd4j.shade.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * Created by Alex on 4/03/2016.
 */
@Data
@JsonIgnoreProperties({"inputSchema", "columnIdx", "stateNames", "statesMap"})
public class CategoricalToOneHotTransform extends BaseTransform {

    private String columnName;
    private int columnIdx = -1;

    private List<String> stateNames;
    private Map<String, Integer> statesMap;

    public CategoricalToOneHotTransform(@JsonProperty("columnName") String columnName) {
        this.columnName = columnName;
    }

    @Override
    public void setInputSchema(Schema inputSchema) {
        super.setInputSchema(inputSchema);

        columnIdx = inputSchema.getIndexOfColumn(columnName);
        ColumnMetaData meta = inputSchema.getMetaData(columnName);
        if (!(meta instanceof CategoricalMetaData))
            throw new IllegalStateException("Cannot convert column \"" + columnName
                            + "\" from categorical to one-hot: column is not categorical (is: " + meta.getColumnType()
                            + ")");
        this.stateNames = ((CategoricalMetaData) meta).getStateNames();

        this.statesMap = new HashMap<>(stateNames.size());
        for (int i = 0; i < stateNames.size(); i++) {
            this.statesMap.put(stateNames.get(i), i);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CategoricalToOneHotTransform o2 = (CategoricalToOneHotTransform) o;

        return columnName.equals(o2.columnName);
    }

    @Override
    public int hashCode() {
        return columnName.hashCode();
    }

    @Override
    public String toString() {
        return "CategoricalToOneHotTransform(columnName=\"" + columnName + "\")";

    }

    @Override
    public Schema transform(Schema schema) {
        List<String> origNames = schema.getColumnNames();
        List<ColumnMetaData> origMeta = schema.getColumnMetaData();

        int i = 0;
        Iterator<String> namesIter = origNames.iterator();
        Iterator<ColumnMetaData> typesIter = origMeta.iterator();

        List<ColumnMetaData> newMeta = new ArrayList<>(schema.numColumns());

        while (namesIter.hasNext()) {
            String s = namesIter.next();
            ColumnMetaData t = typesIter.next();

            if (i++ == columnIdx) {
                //Convert this to one-hot:
                for (String stateName : stateNames) {
                    String newName = s + "[" + stateName + "]";
                    newMeta.add(new IntegerMetaData(newName, 0, 1));
                }
            } else {
                newMeta.add(t);
            }
        }

        return schema.newSchema(newMeta);
    }

    @Override
    public List<Writable> map(List<Writable> writables) {
        if (writables.size() != inputSchema.numColumns()) {
            throw new IllegalStateException("Cannot execute transform: input writables list length (" + writables.size()
                            + ") does not " + "match expected number of elements (schema: " + inputSchema.numColumns()
                            + "). Transform = " + toString());
        }
        int idx = getColumnIdx();

        int n = stateNames.size();
        List<Writable> out = new ArrayList<>(writables.size() + n);

        int i = 0;
        for (Writable w : writables) {

            if (i++ == idx) {
                //Do conversion
                String str = w.toString();
                Integer classIdx = statesMap.get(str);
                if (classIdx == null) {
                    throw new IllegalStateException("Cannot convert categorical value to one-hot: input value (\"" + str
                            + "\") is not in the list of known categories (state names/categories: " + stateNames + ")");
                }
                for (int j = 0; j < n; j++) {
                    if (j == classIdx)
                        out.add(new IntWritable(1));
                    else
                        out.add(new IntWritable(0));
                }
            } else {
                //No change to this column
                out.add(w);
            }
        }
        return out;
    }

    /**
     * Transform an object
     * in to another object
     *
     * @param input the record to transform
     * @return the transformed writable
     */
    @Override
    public Object map(Object input) {
        String str = input.toString();
        List<Integer> oneHot = new ArrayList<>();
        int n = stateNames.size();
        Integer classIdx = statesMap.get(str);
        if (classIdx == null) {
            throw new IllegalStateException("Cannot convert categorical value to one-hot: input value (\"" + str
                    + "\") is not in the list of known categories (state names/categories: " + stateNames + ")");
        }
        for (int j = 0; j < n; j++) {
            if (j == classIdx)
                oneHot.add(1);
            else
                oneHot.add(0);
        }
        return oneHot;
    }

    /**
     * Transform a sequence
     *
     * @param sequence
     */
    @Override
    public Object mapSequence(Object sequence) {
        List<?> values = (List<?>) sequence;
        List<List<Integer>> ret = new ArrayList<>();
        for (Object obj : values) {
            ret.add((List<Integer>) map(obj));
        }
        return ret;
    }

    /**
     * The output column name
     * after the operation has been applied
     *
     * @return the output column name
     */
    @Override
    public String outputColumnName() {
        throw new UnsupportedOperationException("Output column name will be more than 1");
    }

    /**
     * The output column names
     * This will often be the same as the input
     *
     * @return the output column names
     */
    @Override
    public String[] outputColumnNames() {
        return stateNames.toArray(new String[stateNames.size()]);
    }

    /**
     * Returns column names
     * this op is meant to run on
     *
     * @return
     */
    @Override
    public String[] columnNames() {
        return new String[] {columnName};
    }

    /**
     * Returns a singular column name
     * this op is meant to run on
     *
     * @return
     */
    @Override
    public String columnName() {
        return columnName;
    }
}
