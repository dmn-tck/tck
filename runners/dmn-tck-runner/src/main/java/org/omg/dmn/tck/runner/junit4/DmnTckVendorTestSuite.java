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

package org.omg.dmn.tck.runner.junit4;

import org.omg.dmn.tck.marshaller._20160719.TestCases;

import java.net.URI;
import java.util.List;

public interface DmnTckVendorTestSuite {

    List<URI> getTestCases();

    TestSuiteContext createContext();

    void beforeTestCases(TestSuiteContext context, TestCases testCases, URI modelURI );

    void beforeTest(TestSuiteContext context, TestCases.TestCase testCase );

    TestResult executeTest(TestSuiteContext context, TestCases.TestCase testCase );

    void afterTest(TestSuiteContext context, TestCases.TestCase testCase );

    void afterTestCase( TestSuiteContext context, TestCases testCases );

}
