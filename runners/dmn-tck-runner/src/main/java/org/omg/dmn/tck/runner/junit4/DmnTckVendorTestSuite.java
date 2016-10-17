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
import java.net.URL;
import java.util.List;

/**
 * An interface to allow the TCK runner integration
 * with vendor engines.
 *
 * A vendor that would like to use the jUnit runner
 * to execute TCK tests needs to create a class that
 * implements this interface and annotate the class
 * with:
 *
 * <code>@RunWith( DmnTckSuite.class )</code>
 *
 */
public interface DmnTckVendorTestSuite {

    /**
     * Returns a list of URI for all the test
     * case <b>FOLDERS</b> containing each individual
     * test.
     *
     * @return
     */
    List<URL> getTestCases();

    /**
     * Creates a context object to share vendor specific
     * data between lifecycle calls.
     *
     * This is not mandatory, and can return null if the
     * vendor desires. The runner itself just passes
     * forward the context object returned by this method
     * call into each subsequent call.
     *
     * @return
     */
    TestSuiteContext createContext();

    /**
     * A callback to give vendors an opportunity for initialization
     * before running each test file.
     *
     * @param context the context created by the <code>createContext()</code>
     *                call.
     * @param testCases the parsed content of a test file.
     *
     * @param modelURI the resolved URI to the DMN model file.
     */
    void beforeTestCases(TestSuiteContext context, TestCases testCases, URL modelURL );

    /**
     * A callback to give vendors an opportunity to prepare for a
     * single test execution.
     *
     * @param context the context created by the <code>createContext()</code>
     *                call.
     * @param testCase a single test case that will be executed next.
     */
    void beforeTest(TestSuiteContext context, TestCases.TestCase testCase );

    /**
     * Executes a single test case and returns the result.
     *
     * @param context the context created by the <code>createContext()</code>
     *                call.
     * @param testCase a single test case that will be executed next.
     *
     * @return the result of the test execution. Optionally it includes
     *         a string message.
     */
    TestResult executeTest(TestSuiteContext context, TestCases.TestCase testCase );

    /**
     * A callback to give vendors an opportunity to clean up after a
     * single test execution.
     *
     * @param context the context created by the <code>createContext()</code>
     *                call.
     * @param testCase a single test case that will be executed next.
     */
    void afterTest(TestSuiteContext context, TestCases.TestCase testCase );

    /**
     * A callback to give vendors an opportunity for clean up after a
     * full test file.
     *
     * @param context the context created by the <code>createContext()</code>
     *                call.
     * @param testCases the parsed content of a test file.
     *
     */
    void afterTestCase( TestSuiteContext context, TestCases testCases );

}
