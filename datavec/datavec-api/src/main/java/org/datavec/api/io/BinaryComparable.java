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

package org.datavec.api.io;

/**
 * Interface supported by {@link org.apache.hadoop.io.WritableComparable}
 * types supporting ordering/permutation by a representative set of bytes.
 */
public abstract class BinaryComparable implements Comparable<BinaryComparable> {

    /**
     * Return n st bytes 0..n-1 from {#getBytes()} are valid.
     */
    public abstract int getLength();

    /**
     * Return representative byte array for this instance.
     */
    public abstract byte[] getBytes();

    /**
     * Compare bytes from {#getBytes()}.
     * @see org.apache.hadoop.io.WritableComparator#compareBytes(byte[],int,int,byte[],int,int)
     */
    public int compareTo(BinaryComparable other) {
        if (this == other)
            return 0;
        return WritableComparator.compareBytes(getBytes(), 0, getLength(), other.getBytes(), 0, other.getLength());
    }

    /**
     * Compare bytes from {#getBytes()} to those provided.
     */
    public int compareTo(byte[] other, int off, int len) {
        return WritableComparator.compareBytes(getBytes(), 0, getLength(), other, off, len);
    }

    /**
     * Return true if bytes from {#getBytes()} match.
     */
    public boolean equals(Object other) {
        if (!(other instanceof BinaryComparable))
            return false;
        BinaryComparable that = (BinaryComparable) other;
        if (this.getLength() != that.getLength())
            return false;
        return this.compareTo(that) == 0;
    }

    /**
     * Return a hash of the bytes returned from {#getBytes()}.
     * @see org.apache.hadoop.io.WritableComparator#hashBytes(byte[],int)
     */
    public int hashCode() {
        return WritableComparator.hashBytes(getBytes(), getLength());
    }

}
