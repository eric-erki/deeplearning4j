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

package org.nd4j.kryo;

import com.esotericsoftware.kryo.Kryo;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import org.apache.spark.serializer.KryoRegistrator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.common.primitives.AtomicDouble;
import org.nd4j.kryo.primitives.AtomicDoubleSerializer;

/**
 * Spark KryoRegistrator for using Nd4j with Spark + Kryo
 * Use via:
 * sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
 * sparkConf.set("spark.kryo.registrator", "org.nd4j.kryo.Nd4jRegistrator");
 *
 * @author Alex Black
 */
public class Nd4jRegistrator implements KryoRegistrator {
    @Override
    public void registerClasses(Kryo kryo) {
        kryo.register(Nd4j.getBackend().getNDArrayClass(), new Nd4jSerializer());
        kryo.register(AtomicDouble.class, new AtomicDoubleSerializer());

        //Also register Java types (synchronized/unmodifiable collections), which will fail by default
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);
    }
}
