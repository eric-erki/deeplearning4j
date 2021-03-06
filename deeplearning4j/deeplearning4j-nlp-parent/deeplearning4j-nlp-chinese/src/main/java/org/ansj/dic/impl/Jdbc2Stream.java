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

package org.ansj.dic.impl;

import org.ansj.dic.PathToStream;
import org.ansj.exception.LibraryException;
import org.deeplearning4j.common.config.DL4JClassLoading;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * jdbc:mysql://192.168.10.103:3306/infcn_mss?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull|username|password|select name as name,nature,freq from dic where type=1
 *
 * @author ansj
 */
public class Jdbc2Stream extends PathToStream {

    private static final byte[] TAB = "\t".getBytes();

    private static final byte[] LINE = "\n".getBytes();

    private static final String[] JDBC_DRIVERS = {
            "org.h2.Driver",
            "com.ibm.db2.jcc.DB2Driver",
            "org.hsqldb.jdbcDriver",
            "org.gjt.mm.mysql.Driver",
            "oracle.jdbc.OracleDriver",
            "org.postgresql.Driver",
            "net.sourceforge.jtds.jdbc.Driver",
            "com.microsoft.sqlserver.jdbc.SQLServerDriver",
            "org.sqlite.JDBC",
            "com.mysql.jdbc.Driver"
    };

    static {
        loadJdbcDrivers();
    }

    static void loadJdbcDrivers() {
        for (String driverClassName : JDBC_DRIVERS) {
            DL4JClassLoading.loadClassByName(driverClassName);
        }
    }

    @Override
    public InputStream toStream(String path) {
        path = path.substring(7);

        String[] split = path.split("\\|");

        String jdbc = split[0];

        String username = split[1];

        String password = split[2];

        String sqlStr = split[3];

        String logStr = jdbc + "|" + username + "|********|" + sqlStr;

        try (Connection conn = DriverManager.getConnection(jdbc, username, password);
                        PreparedStatement statement = conn.prepareStatement(sqlStr);
                        ResultSet rs = statement.executeQuery();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream(100 * 1024)) {

            int i, count;
            while (rs.next()) {
                for (i = 1, count = rs.getMetaData().getColumnCount(); i < count; ++i) {
                    baos.write(String.valueOf(rs.getObject(i)).getBytes());
                    baos.write(TAB);
                }
                baos.write(String.valueOf(rs.getObject(count)).getBytes());
                baos.write(LINE);
            }

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            throw new LibraryException("err to load by jdbc " + logStr);
        }
    }

    public static String encryption(String path) {

        String[] split = path.split("\\|");

        String jdbc = split[0];

        String username = split[1];

        String password = split[2];

        String sqlStr = split[3];

        return jdbc + "|" + username + "|********|" + sqlStr;
    }
}
