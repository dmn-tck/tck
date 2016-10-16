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

package org.omg.dmn.tck.runner.drools;

import org.junit.runner.RunWith;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.runner.junit4.DmnTckSuite;
import org.omg.dmn.tck.runner.junit4.DmnTckVendorTestSuite;
import org.omg.dmn.tck.runner.junit4.TestResult;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RunWith( DmnTckSuite.class )
public class DroolsTCKTest
        implements DmnTckVendorTestSuite {

    public List<URI> getTestCases() {
        List testCases = new ArrayList(  );
        File cl2parent = new File("../../TestCases/compliance-level-2");
        FilenameFilter filenameFilter = (dir, name) -> name.matches( "\\d\\d\\d\\d-.*" );
        for( File file : cl2parent.listFiles( filenameFilter ) ) {
            testCases.add( file.toURI() );
        }
        File cl3parent = new File("../../TestCases/compliance-level-3");
        for( File file : cl3parent.listFiles( filenameFilter ) ) {
            testCases.add( file.toURI() );
        }
        return testCases;
    }

    public TestSuiteContext createContext() {
        return null;
    }

    public void beforeTestCases(TestSuiteContext context, TestCases testCases, URI modelURI ) {
        System.out.println("Before test case for model: "+modelURI);
    }

    public void beforeTest(TestSuiteContext context, TestCases.TestCase testCase) {
        System.out.println("Before test: "+testCase.getId());
    }

    public TestResult executeTest(TestSuiteContext context, TestCases.TestCase testCase) {
        TestResult.Result r = TestResult.Result.SUCCESS;
        return new TestResult( r, "Yey" );
    }

    public void afterTest(TestSuiteContext context, TestCases.TestCase testCase) {
        System.out.println("After test: "+testCase.getId());
    }

    public void afterTestCase(TestSuiteContext context, TestCases testCases) {
        System.out.println("After test case.");
    }

}
