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

public class TableRow {
    private List<String>  icons;
    private List<String>  text;
    private List<Integer> rowspan;

    public TableRow() {
        this( new ArrayList<>(), new ArrayList<>(), new ArrayList<>() );
    }

    public TableRow(List<String> icons, List<String> text, List<Integer> rowspan) {
        this.icons = icons;
        this.text = text;
        this.rowspan = rowspan;
    }

    public List<String> getIcons() {
        return icons;
    }

    public void setIcons(List<String> icons) {
        this.icons = icons;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public List<Integer> getRowspan() {
        return rowspan;
    }

    public void setRowspan(List<Integer> rowspan) {
        this.rowspan = rowspan;
    }
}
