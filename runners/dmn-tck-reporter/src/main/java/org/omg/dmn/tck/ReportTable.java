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

package org.omg.dmn.tck;

import java.util.ArrayList;
import java.util.List;

public class ReportTable {
    private String         title;
    private List<String>   headers;
    private List<String>   headerDetails;
    private List<TableRow> rows;
    private List<String>   totals;

    public ReportTable() {
        this( "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(  ) );
    }

    public ReportTable(String title, List<String> headers, List<String> headerDetails, List<TableRow> rows, List<String> totals) {
        this.title = title;
        this.headers = headers;
        this.headerDetails = headerDetails;
        this.rows = rows;
        this.totals = totals;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<String> getHeaderDetails() {
        return headerDetails;
    }

    public List<TableRow> getRows() {
        return rows;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public void setHeaderDetails(List<String> headerDetails) {
        this.headerDetails = headerDetails;
    }

    public void setRows(List<TableRow> rows) {
        this.rows = rows;
    }

    public List<String> getTotals() {
        return totals;
    }

    public void setTotals(List<String> totals) {
        this.totals = totals;
    }
}
