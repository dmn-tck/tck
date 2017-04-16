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

import org.omg.dmn.tck.marshaller._20160719.TestCases;

public class TestCasesData {
    public String    folder;
    public String    testCaseName;
    public TestCases model;

    public TestCasesData(String folder, String testCaseName, TestCases model) {
        this.folder = folder;
        this.testCaseName = testCaseName;
        this.model = model;
    }
}
