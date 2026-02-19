/*
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
package org.omg.dmn.tck.runner.quantumdmn;

import org.omg.dmn.tck.runner.junit4.TestSuiteContext;

/**
 * Context holder for the QuantumDMN TCK test suite.
 * Stores the DMN XML content that has been loaded for the current test file.
 */
public class QuantumDmnContext implements TestSuiteContext {

    private String dmnXml;
    private String modelName;
    private String dmnFileName;

    public String getDmnFileName() {
        return dmnFileName;
    }

    public void setDmnFileName(String dmnFileName) {
        this.dmnFileName = dmnFileName;
    }

    public String getDmnXml() {
        return dmnXml;
    }

    public void setDmnXml(String dmnXml) {
        this.dmnXml = dmnXml;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    private java.util.List<String> additionalXmls = new java.util.ArrayList<>();

    public java.util.List<String> getAdditionalXmls() {
        return additionalXmls;
    }

    public void setAdditionalXmls(java.util.List<String> additionalXmls) {
        this.additionalXmls = additionalXmls;
    }
}
