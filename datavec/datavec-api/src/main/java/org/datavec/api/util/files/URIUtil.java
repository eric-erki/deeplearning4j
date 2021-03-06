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

package org.datavec.api.util.files;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Lightweight utilities for converting files to URI.
 *
 * @author Justin Long (crockpotveggies)
 */
public class URIUtil {

    public static URI fileToURI(File f) {
        try {
            // manually construct URI (this is faster)
            String sp = slashify(f.getAbsoluteFile().getPath(), false);
            if (!sp.startsWith("//"))
                sp = "//" + sp;
            return new URI("file", null, sp, null);

        } catch (URISyntaxException x) {
            throw new Error(x); // Can't happen
        }
    }

    private static String slashify(String path, boolean isDirectory) {
        String p = path;
        if (File.separatorChar != '/')
            p = p.replace(File.separatorChar, '/');
        if (!p.startsWith("/"))
            p = "/" + p;
        if (!p.endsWith("/") && isDirectory)
            p = p + "/";
        return p;
    }
}
