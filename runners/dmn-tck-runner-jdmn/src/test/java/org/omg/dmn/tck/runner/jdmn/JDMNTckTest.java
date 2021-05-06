/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.omg.dmn.tck.runner.jdmn;

import com.gs.dmn.dialect.StandardDMNDialectDefinition;
import com.gs.dmn.feel.lib.StandardFEELLib;
import com.gs.dmn.log.BuildLogger;
import com.gs.dmn.log.Slf4jBuildLogger;
import com.gs.dmn.runtime.interpreter.DMNInterpreter;
import com.gs.dmn.runtime.interpreter.Result;
import com.gs.dmn.serialization.DMNReader;
import com.gs.dmn.serialization.DMNWriter;
import com.gs.dmn.tck.TCKUtil;
import com.gs.dmn.transformation.DMNTransformer;
import com.gs.dmn.transformation.ToSimpleNameTransformer;
import com.gs.dmn.transformation.basic.BasicDMNToJavaTransformer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.marshaller._20160719.TestCases.TestCase;
import org.omg.dmn.tck.marshaller._20160719.TestCases.TestCase.ResultNode;
import org.omg.dmn.tck.runner.junit4.DmnTckSuite;
import org.omg.dmn.tck.runner.junit4.DmnTckVendorTestSuite;
import org.omg.dmn.tck.runner.junit4.TestResult;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RunWith(DmnTckSuite.class)
public class JDMNTckTest implements DmnTckVendorTestSuite {
    private static final BuildLogger LOGGER = new Slf4jBuildLogger(LoggerFactory.getLogger(JDMNTckTest.class));

    private static final File CL2_FOLDER = new File("TestCases/compliance-level-2");
    private static final File CL3_FOLDER = new File("TestCases/compliance-level-3");
    private static final File NON_COMPLIANT_FOLDER = new File("TestCases/non-compliant");
    private static final boolean IGNORE_ERROR_FLAG = true;

    @Override
    public List<URL> getTestCases() {
        try {
            List<URL> urls = new ArrayList<>();
            addTestFolders(urls, CL2_FOLDER);
            addTestFolders(urls, CL3_FOLDER);
//            File file = new File("../../" + CL3_FOLDER.getPath(), "0034-drg-scopes");
//            urls.add(file.toURI().toURL());
            return urls;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addTestFolders(List<URL> urls, File folder) throws MalformedURLException {
        File testFile = new File("../../" + folder.getPath());
        for (File file : testFile.listFiles()) {
            if (file.isDirectory()) {
                urls.add(file.toURI().toURL());
            }
        }
    }

    @Override
    public TestSuiteContext createContext() {
        DMNReader dmnReader = new DMNReader(LOGGER, false);
        DMNWriter dmnWriter = new DMNWriter(LOGGER);
        DMNTransformer<TestCases> dmnTransformer = new ToSimpleNameTransformer(LOGGER);
        StandardDMNDialectDefinition dialectDefinition = new StandardDMNDialectDefinition();
        return new JDMNTestContext<>(dmnReader, dmnWriter, dmnTransformer, dialectDefinition);
    }

    @Override
    public void beforeTestCases(TestSuiteContext context, TestCases testCases, URL modelURL, Collection<? extends URL> additionalModels) {
        ((JDMNTestContext<?, ?, ?, ?, ?>) context).prepareModel(modelURL, additionalModels, LOGGER);
        ((JDMNTestContext<?, ?, ?, ?, ?>) context).clean(testCases);
        ((JDMNTestContext<?, ?, ?, ?, ?>) context).setTestCases(testCases);
    }

    @Override
    public void beforeTestCases(TestSuiteContext context, TestCases testCases, URL modelURL) {
        ((JDMNTestContext) context).prepareModel(modelURL, Collections.emptyList(), LOGGER);
        ((JDMNTestContext) context).clean(testCases);
        ((JDMNTestContext) context).setTestCases(testCases);
    }

    @Override
    public void beforeTest(Description description, TestSuiteContext context, TestCase testCase) {
    }

    @Override
    public TestResult executeTest(Description description, TestSuiteContext context, TestCase testCase) {
        List<String> failures = new ArrayList<>();
        List<String> exceptions = new ArrayList<>();

        try {
            JDMNTestContext gsContext = (JDMNTestContext) context;
            BasicDMNToJavaTransformer basicTransformer = gsContext.getBasicToJavaTransformer();
            StandardFEELLib lib = gsContext.getLib();
            DMNInterpreter interpreter = gsContext.getInterpreter();
            TCKUtil tckUtil = new TCKUtil(basicTransformer, lib);

            TestCases testCases = gsContext.getTestCases();
            for (ResultNode res: testCase.getResultNode()) {
                String testLocation = String.format("Unexpected result in test for model '%s', TestCase.id='%s', ResultNode.name='%s'.", testCases.getModelName(), testCase.getId(), res.getName());
                Object expectedValue = null;
                Result actualResult = null;
                Object actualValue = null;
                try {
                    expectedValue = tckUtil.expectedValue(testCases, testCase, res);
                    actualResult = tckUtil.evaluate(interpreter, testCases, testCase, res);
                    actualValue = Result.value(actualResult);
                    if (!isEquals(expectedValue, actualValue)) {
                        String errorMessage = String.format("%s ResultNode '%s' output mismatch, expected '%s' actual '%s'", testLocation, res.getName(), expectedValue, actualValue);
                        failures.add(errorMessage);
                    }
                    if (!IGNORE_ERROR_FLAG) {
                        String errorFlagMessage = String.format("%s ResultNode '%s' error flag mismatch", testLocation, res.getName());
                        if (!isEquals(res.isErrorResult(), actualResult.hasErrors())) {
                            failures.add(errorFlagMessage);
                        }
                    }
                } catch (Throwable e) {
                    String stackTrace = ExceptionUtils.getStackTrace(e);
                    LOGGER.error(stackTrace);
                    String errorMessage = String.format("%s ResultNode '%s' output mismatch, expected '%s' actual '%s'", testLocation, res.getName(), expectedValue, actualValue);
                    exceptions.add(errorMessage + ". Exception thrown while testing");
                }
            }
        } catch (Throwable e) {
            exceptions.add(e.getMessage());
        }

        TestResult.Result r = TestResult.Result.SUCCESS;
        String message = "";
        if (!failures.isEmpty()) {
            r = TestResult.Result.ERROR;
            message = String.join("\n", failures);
        }
        if (!exceptions.isEmpty()) {
            r = TestResult.Result.ERROR;
            message = String.join("\n", exceptions);
        }
        return new TestResult(r, message);
    }

    private boolean isEquals(Object expectedValue, Object actualValue) {
        try {
            com.gs.dmn.runtime.Assert.assertEquals(expectedValue, actualValue);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    @Override
    public void afterTest(Description description, TestSuiteContext context, TestCase testCase) {
    }

    @Override
    public void afterTestCase(TestSuiteContext context, TestCases testCases) {
    }
}
