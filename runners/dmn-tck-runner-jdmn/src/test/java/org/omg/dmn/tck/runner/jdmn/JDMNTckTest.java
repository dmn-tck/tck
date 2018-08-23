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
import com.gs.dmn.feel.analysis.semantics.environment.Environment;
import com.gs.dmn.feel.analysis.semantics.environment.EnvironmentFactory;
import com.gs.dmn.feel.analysis.semantics.type.Type;
import com.gs.dmn.feel.lib.FEELLib;
import com.gs.dmn.feel.lib.StandardFEELLib;
import com.gs.dmn.feel.synthesis.FEELTranslator;
import com.gs.dmn.feel.synthesis.FEELTranslatorImpl;
import com.gs.dmn.log.BuildLogger;
import com.gs.dmn.log.Slf4jBuildLogger;
import com.gs.dmn.runtime.interpreter.DMNInterpreter;
import com.gs.dmn.runtime.interpreter.environment.RuntimeEnvironment;
import com.gs.dmn.serialization.DMNReader;
import com.gs.dmn.serialization.DMNWriter;
import com.gs.dmn.tck.TCKUtil;
import com.gs.dmn.transformation.DMNTransformer;
import com.gs.dmn.transformation.ToSimpleNameTransformer;
import com.gs.dmn.transformation.basic.BasicDMN2JavaTransformer;
import com.gs.dmn.transformation.basic.QualifiedName;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.marshaller._20160719.TestCases.TestCase;
import org.omg.dmn.tck.marshaller._20160719.TestCases.TestCase.ResultNode;
import org.omg.dmn.tck.runner.junit4.DmnTckSuite;
import org.omg.dmn.tck.runner.junit4.DmnTckVendorTestSuite;
import org.omg.dmn.tck.runner.junit4.TestResult;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;
import org.omg.spec.dmn._20180521.model.TDecision;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(DmnTckSuite.class)
public class JDMNTckTest implements DmnTckVendorTestSuite {
    private static final BuildLogger LOGGER = new Slf4jBuildLogger(LoggerFactory.getLogger(JDMNTckTest.class));

    static final File CL2_FOLDER = new File("TestCases/compliance-level-2");
    static final File CL3_FOLDER = new File("TestCases/compliance-level-3");
    static final File NON_COMPLIANT_FOLDER = new File("TestCases/non-compliant");

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
        return new JDMNTestContext(dmnReader, dmnWriter, dmnTransformer, dialectDefinition);
    }

    @Override
    public void beforeTestCases(TestSuiteContext context, TestCases testCases, URL modelURL) {
        ((JDMNTestContext)context).prepareModel(modelURL, LOGGER);
        ((JDMNTestContext)context).clean(testCases);
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
            BasicDMN2JavaTransformer basicTransformer = gsContext.getBasicToJavaTransformer();
            StandardFEELLib lib = gsContext.getLib();
            DMNInterpreter interpreter = gsContext.getInterpreter();
            TCKUtil tckUtil = new TCKUtil(basicTransformer, lib);

            RuntimeEnvironment runtimeEnvironment =  tckUtil.makeEnvironment(testCase);

            List<ResultNode> resultNode = testCase.getResultNode();
            for (int i = 0; i < resultNode.size(); i++) {
                ResultNode res = resultNode.get(i);
                Object expectedValue = null;
                Object actualOutput = null;
                try {
                    String decisionName = res.getName();
                    TDecision decision = (TDecision) basicTransformer.getDMNModelRepository().findDRGElementByName(decisionName);
                    Environment environment = basicTransformer.makeEnvironment(decision);
                    Type decisionType = basicTransformer.drgElementOutputFEELType(decision, environment);
                    expectedValue = tckUtil.makeValue(res.getExpected(), decisionType);
                    actualOutput = interpreter.evaluate(decisionName, runtimeEnvironment);
                } catch (Throwable e) {
                    if (!res.isErrorResult()) {
                        String errorMessage = String.format("Expected error for result node '%s' at position %d %s", res.getName(), i, e.getMessage());
                        failures.add(errorMessage);
                    }
                }
                if (!isEquals(expectedValue, actualOutput)) {
                    String errorMessage = String.format("Decision '%s' output mismatch, expected '%s' actual '%s'", res.getName(), expectedValue, actualOutput);
                    failures.add(errorMessage);
                }
            }
        } catch (Throwable e) {
            exceptions.add(e.getMessage());
        }

        TestResult.Result r = TestResult.Result.SUCCESS;
        String message = "";
        if (!failures.isEmpty()) {
            r = TestResult.Result.ERROR;
            message = failures.stream().collect(Collectors.joining("\n"));
        }
        if (!exceptions.isEmpty()) {
            r = TestResult.Result.ERROR;
            message = exceptions.stream().collect(Collectors.joining("\n"));
        }
        return new TestResult(r, message);
    }

    private boolean isEquals(Object expectedValue, Object actualValue) {
        try {
            com.gs.dmn.runtime.Assert.assertEquals(expectedValue, actualValue);
            return true;
        } catch (Exception e) {
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
