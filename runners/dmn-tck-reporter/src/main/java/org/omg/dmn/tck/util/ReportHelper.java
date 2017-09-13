/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omg.dmn.tck.util;

import org.omg.dmn.tck.ReportTable;
import org.omg.dmn.tck.TableRow;

public class ReportHelper {

    public static void addRowCell(TableRow row, String text, String icon) {
        row.getText().add( text );
        row.getIcons().add( icon );
        row.getRowspan().add( 1 );
    }

    public static void addHeader(ReportTable table, String t, String d) {
        table.getHeaders().add( t );
        table.getHeaderDetails().add( d );
    }

    public static String removeQuotes(String val) {
        if( val.length() >= 2 && val.startsWith( "\"" ) && val.endsWith( "\"" ) ) {
            return val.substring( 1, val.length()-1 );
        }
        return val;
    }
}
