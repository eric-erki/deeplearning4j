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

package org.datavec.api.transform.sequence;

import org.datavec.api.transform.ReduceOp;
import org.datavec.api.transform.Transform;
import org.datavec.api.transform.reduce.Reducer;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.schema.SequenceSchema;
import org.datavec.api.transform.sequence.window.ReduceSequenceByWindowTransform;
import org.datavec.api.transform.sequence.window.TimeWindowFunction;
import org.datavec.api.transform.sequence.window.WindowFunction;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.LongWritable;
import org.datavec.api.writable.NullWritable;
import org.datavec.api.writable.Writable;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.nd4j.common.tests.BaseND4JTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by Alex on 16/04/2016.
 */
public class TestReduceSequenceByWindowFunction extends BaseND4JTest {

    @Test
    public void testReduceSequenceByWindowFunction() {
        //Time windowing: 1 second (1000 milliseconds) window

        //Create some data.
        List<List<Writable>> sequence = new ArrayList<>();
        //First window:
        sequence.add(Arrays.asList((Writable) new LongWritable(1451606400000L), new IntWritable(0)));
        sequence.add(Arrays.asList((Writable) new LongWritable(1451606400000L + 100L), new IntWritable(1)));
        sequence.add(Arrays.asList((Writable) new LongWritable(1451606400000L + 200L), new IntWritable(2)));
        //Second window:
        sequence.add(Arrays.asList((Writable) new LongWritable(1451606400000L + 1000L), new IntWritable(3)));
        //Third window: empty
        //Fourth window:
        sequence.add(Arrays.asList((Writable) new LongWritable(1451606400000L + 3000L), new IntWritable(4)));
        sequence.add(Arrays.asList((Writable) new LongWritable(1451606400000L + 3100L), new IntWritable(5)));

        Schema schema = new SequenceSchema.Builder().addColumnTime("timecolumn", DateTimeZone.UTC)
                        .addColumnInteger("intcolumn").build();

        WindowFunction wf = new TimeWindowFunction("timecolumn", 1, TimeUnit.SECONDS);
        wf.setInputSchema(schema);


        //Now: reduce by summing...
        Reducer reducer = new Reducer.Builder(ReduceOp.Sum).takeFirstColumns("timecolumn").build();

        Transform transform = new ReduceSequenceByWindowTransform(reducer, wf);
        transform.setInputSchema(schema);

        List<List<Writable>> postApply = transform.mapSequence(sequence);
        assertEquals(4, postApply.size());


        List<Writable> exp0 = Arrays.asList((Writable) new LongWritable(1451606400000L), new IntWritable(0 + 1 + 2));
        assertEquals(exp0, postApply.get(0));

        List<Writable> exp1 = Arrays.asList((Writable) new LongWritable(1451606400000L + 1000L), new IntWritable(3));
        assertEquals(exp1, postApply.get(1));

        // here, takefirst of an empty window -> nullwritable makes more sense
        List<Writable> exp2 = Arrays.asList((Writable) NullWritable.INSTANCE, NullWritable.INSTANCE);
        assertEquals(exp2, postApply.get(2));

        List<Writable> exp3 = Arrays.asList((Writable) new LongWritable(1451606400000L + 3000L), new IntWritable(9));
        assertEquals(exp3, postApply.get(3));
    }

}
