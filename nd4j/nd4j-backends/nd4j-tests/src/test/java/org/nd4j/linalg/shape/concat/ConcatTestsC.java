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

package org.nd4j.linalg.shape.concat;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.checkutil.NDArrayCreationUtil;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.common.primitives.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Adam Gibson
 */
@Slf4j
@RunWith(Parameterized.class)
public class ConcatTestsC extends BaseNd4jTest {

    public ConcatTestsC(Nd4jBackend backend) {
        super(backend);
    }


    @Test
    public void testConcatVertically() {
        INDArray rowVector = Nd4j.ones(1, 5);
        INDArray other = Nd4j.ones(1, 5);
        INDArray concat = Nd4j.vstack(other, rowVector);
        assertEquals(rowVector.rows() * 2, concat.rows());
        assertEquals(rowVector.columns(), concat.columns());

        INDArray arr2 = Nd4j.create(5, 5);
        INDArray slice1 = arr2.slice(0);
        INDArray slice2 = arr2.slice(1);
        INDArray arr3 = Nd4j.create(2, 5);
        INDArray vstack = Nd4j.vstack(slice1, slice2);
        assertEquals(arr3, vstack);

        INDArray col1 = arr2.getColumn(0).reshape(5, 1);
        INDArray col2 = arr2.getColumn(1).reshape(5, 1);
        INDArray vstacked = Nd4j.vstack(col1, col2);
        assertEquals(Nd4j.create(10, 1), vstacked);
    }


    @Test
    public void testConcatScalars() {
        INDArray first = Nd4j.arange(0, 1).reshape(1, 1);
        INDArray second = Nd4j.arange(0, 1).reshape(1, 1);
        INDArray firstRet = Nd4j.concat(0, first, second);
        assertTrue(firstRet.isColumnVector());
        INDArray secondRet = Nd4j.concat(1, first, second);
        assertTrue(secondRet.isRowVector());
    }

    @Test
    public void testConcatScalars1() {
        INDArray first = Nd4j.scalar(1);
        INDArray second = Nd4j.scalar(2);
        INDArray third = Nd4j.scalar(3);

        INDArray result = Nd4j.concat(0, first, second, third);

        assertEquals(1f, result.getFloat(0), 0.01f);
        assertEquals(2f, result.getFloat(1), 0.01f);
        assertEquals(3f, result.getFloat(2), 0.01f);
    }

    @Test
    public void testConcatVectors1() {
        INDArray first = Nd4j.ones(1, 10);
        INDArray second = Nd4j.ones(1, 10);
        INDArray third = Nd4j.ones(1, 10);

        INDArray result = Nd4j.concat(0, first, second, third);

        assertEquals(3, result.rows());
        assertEquals(10, result.columns());

//        System.out.println(result);

        for (int x = 0; x < 30; x++) {
            assertEquals(1f, result.getFloat(x), 0.001f);
        }
    }

    @Test
    public void testConcatMatrices() {
        INDArray a = Nd4j.linspace(1, 4, 4, DataType.DOUBLE).reshape(2, 2);
        INDArray b = a.dup();


        INDArray concat1 = Nd4j.concat(1, a, b);
        INDArray oneAssertion = Nd4j.create(new double[][] {{1, 2, 1, 2}, {3, 4, 3, 4}});

//        System.out.println("Assertion: " + Arrays.toString(oneAssertion.data().asFloat()));
//        System.out.println("Result: " + Arrays.toString(concat1.data().asFloat()));

        assertEquals(oneAssertion, concat1);

        INDArray concat = Nd4j.concat(0, a, b);
        INDArray zeroAssertion = Nd4j.create(new double[][] {{1, 2}, {3, 4}, {1, 2}, {3, 4}});
        assertEquals(zeroAssertion, concat);
    }

    @Test
    public void testAssign() {
        INDArray vector = Nd4j.linspace(1, 5, 5, Nd4j.dataType());
        vector.assign(1);
        assertEquals(Nd4j.ones(5), vector);
        INDArray twos = Nd4j.ones(2, 2);
        INDArray rand = Nd4j.rand(2, 2);
        twos.assign(rand);
        assertEquals(rand, twos);

        INDArray tensor = Nd4j.rand(new long[]{3L, 3L, 3L});
        INDArray ones = Nd4j.ones(3, 3, 3);
        assertTrue(Arrays.equals(tensor.shape(), ones.shape()));
        ones.assign(tensor);
        assertEquals(tensor, ones);
    }

    @Test
    public void testConcatRowVectors() {
        INDArray rowVector = Nd4j.create(new double[] {1, 2, 3, 4, 5, 6}, new int[] {1, 6});
        INDArray matrix = Nd4j.create(new double[] {7, 8, 9, 10, 11, 12}, new int[] {1, 6});

        INDArray assertion1 = Nd4j.create(new double[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, new int[] {1, 12});
        INDArray assertion0 = Nd4j.create(new double[][] {{1, 2, 3, 4, 5, 6}, {7, 8, 9, 10, 11, 12}});

        INDArray concat1 = Nd4j.hstack(rowVector, matrix);
        INDArray concat0 = Nd4j.vstack(rowVector, matrix);
        assertEquals(assertion1, concat1);
        assertEquals(assertion0, concat0);
    }


    @Test
    public void testConcat3d() {
        INDArray first = Nd4j.linspace(1, 24, 24, Nd4j.dataType()).reshape('c', 2, 3, 4);
        INDArray second = Nd4j.linspace(24, 36, 12, Nd4j.dataType()).reshape('c', 1, 3, 4);
        INDArray third = Nd4j.linspace(36, 48, 12, Nd4j.dataType()).reshape('c', 1, 3, 4);

        //ConcatV2, dim 0
        INDArray exp = Nd4j.create(2 + 1 + 1, 3, 4);
        exp.put(new INDArrayIndex[] {NDArrayIndex.interval(0, 2), NDArrayIndex.all(), NDArrayIndex.all()}, first);
        exp.put(new INDArrayIndex[] {NDArrayIndex.point(2), NDArrayIndex.all(), NDArrayIndex.all()}, second);
        exp.put(new INDArrayIndex[] {NDArrayIndex.point(3), NDArrayIndex.all(), NDArrayIndex.all()}, third);

        INDArray concat0 = Nd4j.concat(0, first, second, third);

        assertEquals(exp, concat0);

        //ConcatV2, dim 1
        second = Nd4j.linspace(24, 32, 8, Nd4j.dataType()).reshape('c', 2, 1, 4);
        for (int i = 0; i < second.tensorsAlongDimension(1); i++) {
            INDArray secondTad = second.tensorAlongDimension(i, 1);
//            System.out.println(second.tensorAlongDimension(i, 1));
        }

        third = Nd4j.linspace(32, 48, 16).reshape('c', 2, 2, 4);
        exp = Nd4j.create(2, 3 + 1 + 2, 4);
        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.interval(0, 3), NDArrayIndex.all()}, first);
        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.point(3), NDArrayIndex.all()}, second);
        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.interval(4, 6), NDArrayIndex.all()}, third);

        INDArray concat1 = Nd4j.concat(1, first, second, third);

        assertEquals(exp, concat1);

        //ConcatV2, dim 2
        second = Nd4j.linspace(24, 36, 12).reshape('c', 2, 3, 2);
        third = Nd4j.linspace(36, 42, 6).reshape('c', 2, 3, 1);
        exp = Nd4j.create(2, 3, 4 + 2 + 1);

        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 4)}, first);
        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(4, 6)}, second);
        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(6)}, third);

        INDArray concat2 = Nd4j.concat(2, first, second, third);

        assertEquals(exp, concat2);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testConcatVector() {
        Nd4j.concat(0, Nd4j.ones(1,1000000), Nd4j.create(1, 1));
    }

    @Test
    @Ignore
    public void testConcat3dv2() {

        INDArray first = Nd4j.linspace(1, 24, 24).reshape('c', 2, 3, 4);
        INDArray second = Nd4j.linspace(24, 35, 12).reshape('c', 1, 3, 4);
        INDArray third = Nd4j.linspace(36, 47, 12).reshape('c', 1, 3, 4);

        //ConcatV2, dim 0
        INDArray exp = Nd4j.create(2 + 1 + 1, 3, 4);
        exp.put(new INDArrayIndex[] {NDArrayIndex.interval(0, 2), NDArrayIndex.all(), NDArrayIndex.all()}, first);
        exp.put(new INDArrayIndex[] {NDArrayIndex.point(2), NDArrayIndex.all(), NDArrayIndex.all()}, second);
        exp.put(new INDArrayIndex[] {NDArrayIndex.point(3), NDArrayIndex.all(), NDArrayIndex.all()}, third);

        List<Pair<INDArray, String>> firsts = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, new int[]{2, 3, 4}, DataType.DOUBLE);
        List<Pair<INDArray, String>> seconds = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, new int[]{1, 3, 4}, DataType.DOUBLE);
        List<Pair<INDArray, String>> thirds = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, new int[]{1, 3, 4}, DataType.DOUBLE);
        for (Pair<INDArray, String> f : firsts) {
            for (Pair<INDArray, String> s : seconds) {
                for (Pair<INDArray, String> t : thirds) {
                    INDArray f2 = f.getFirst().assign(first);
                    INDArray s2 = s.getFirst().assign(second);
                    INDArray t2 = t.getFirst().assign(third);

                    INDArray concat0 = Nd4j.concat(0, f2, s2, t2);
                    if (!exp.equals(concat0)) {
                        concat0 = Nd4j.concat(0, f2, s2, t2);
                    }
                    assertEquals(exp, concat0);
                }
            }
        }

        //ConcatV2, dim 1
        second = Nd4j.linspace(24, 31, 8).reshape('c', 2, 1, 4);
        third = Nd4j.linspace(32, 47, 16).reshape('c', 2, 2, 4);
        exp = Nd4j.create(2, 3 + 1 + 2, 4);
        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.interval(0, 3), NDArrayIndex.all()}, first);
        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.point(3), NDArrayIndex.all()}, second);
        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.interval(4, 6), NDArrayIndex.all()}, third);

        firsts = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, new int[]{2, 3, 4}, DataType.DOUBLE);
        seconds = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, new int[]{2, 1, 4}, DataType.DOUBLE);
        thirds = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, new int[]{2, 2, 4}, DataType.DOUBLE);
        for (Pair<INDArray, String> f : firsts) {
            for (Pair<INDArray, String> s : seconds) {
                for (Pair<INDArray, String> t : thirds) {
                    INDArray f2 = f.getFirst().assign(first);
                    INDArray s2 = s.getFirst().assign(second);
                    INDArray t2 = t.getFirst().assign(third);

                    INDArray concat1 = Nd4j.concat(1, f2, s2, t2);

                    assertEquals(exp, concat1);
                }
            }
        }

        //ConcatV2, dim 2
        second = Nd4j.linspace(24, 35, 12).reshape('c', 2, 3, 2);
        third = Nd4j.linspace(36, 41, 6).reshape('c', 2, 3, 1);
        exp = Nd4j.create(2, 3, 4 + 2 + 1);
        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 4)}, first);
        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(4, 6)}, second);
        exp.put(new INDArrayIndex[] {NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.point(6)}, third);

        firsts = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, new int[]{2, 3, 4}, DataType.DOUBLE);
        seconds = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, new int[]{2, 3, 2}, DataType.DOUBLE);
        thirds = NDArrayCreationUtil.getAll3dTestArraysWithShape(12345, new int[]{2, 3, 1}, DataType.DOUBLE);
        for (Pair<INDArray, String> f : firsts) {
            for (Pair<INDArray, String> s : seconds) {
                for (Pair<INDArray, String> t : thirds) {
                    INDArray f2 = f.getFirst().assign(first);
                    INDArray s2 = s.getFirst().assign(second);
                    INDArray t2 = t.getFirst().assign(third);

                    INDArray concat2 = Nd4j.concat(2, f2, s2, t2);

                    assertEquals(exp, concat2);
                }
            }
        }
    }


    @Test
    public void testLargeConcat() {
        val list = new ArrayList<INDArray>();

        for (int e = 0; e < 20000; e++)
            list.add(Nd4j.create(DataType.INT, 1, 300).assign(e));

        val timeStart = System.nanoTime();
        val result = Nd4j.concat(0, list.toArray(new INDArray[list.size()]));
        val timeEnd = System.nanoTime();

        log.info("Time: {} us", (timeEnd - timeStart) / 1000);

        for (int e = 0; e < 20000; e++)
            assertEquals((float) e, result.getRow(e).meanNumber().floatValue(), 1e-5f);
    }


    @Override
    public char ordering() {
        return 'c';
    }
}
