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

public class TestLabel {
    private String label;
    private String description;
    private String xpath;

    public TestLabel() {
    }

    public TestLabel(String label, String description, String xpath) {
        this.label = label;
        this.description = description;
        this.xpath = xpath;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof TestLabel) ) return false;

        TestLabel testLabel = (TestLabel) o;

        if ( label != null ? !label.equals( testLabel.label ) : testLabel.label != null ) return false;
        if ( description != null ? !description.equals( testLabel.description ) : testLabel.description != null ) return false;
        return xpath != null ? xpath.equals( testLabel.xpath ) : testLabel.xpath == null;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (xpath != null ? xpath.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestLabel{" +
               "label='" + label + '\'' +
               ", description='" + description + '\'' +
               ", xpath='" + xpath + '\'' +
               '}';
    }
}
