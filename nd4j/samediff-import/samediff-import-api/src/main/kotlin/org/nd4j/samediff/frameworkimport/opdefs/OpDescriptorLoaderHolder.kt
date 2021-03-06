/* ******************************************************************************
 * Copyright (c) 2021 Deeplearning4j Contributors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/
package org.nd4j.samediff.frameworkimport.opdefs

import org.nd4j.ir.OpNamespace
import org.nd4j.shade.protobuf.GeneratedMessageV3
import java.util.*

object OpDescriptorLoaderHolder {
    val opDescriptorLoader = HashMap<String,OpDescriptorLoader<out GeneratedMessageV3>>()
    var nd4jOpDescriptor: OpNamespace.OpDescriptorList = loadDescriptorLoaders()

    fun <ARG_DEF_TYPE: GeneratedMessageV3> listForFramework(frameworkName: String): Map<String,ARG_DEF_TYPE> {
        return opDescriptorLoader[frameworkName]!!.inputFrameworkOpDescriptorList() as Map<String,ARG_DEF_TYPE>
    }

    private fun loadDescriptorLoaders(): OpNamespace.OpDescriptorList {
        val loaded = ServiceLoader.load(OpDescriptorLoader::class.java)
        val iter = loaded.iterator()
        while(iter.hasNext()) {
            val next = iter.next()
            val loadedList = next.inputFrameworkOpDescriptorList()
            opDescriptorLoader[next.frameworkName()] = next
            nd4jOpDescriptor = next.nd4jOpList()
        }

        return nd4jOpDescriptor
    }


}