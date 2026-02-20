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

import com.quantumdmn.client.ApiException;
import com.quantumdmn.client.DmnService;
import com.quantumdmn.client.api.DefaultApi;
import com.quantumdmn.client.auth.ZitadelTokenProvider;
import com.quantumdmn.client.model.EvaluateDesignRequest;
import com.quantumdmn.client.model.EvaluationResult;
import com.quantumdmn.client.model.FeelValue;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.marshaller._20160719.TestCaseType;
import org.omg.dmn.tck.runner.junit4.DmnTckSuite;
import org.omg.dmn.tck.runner.junit4.DmnTckVendorTestSuite;
import org.omg.dmn.tck.runner.junit4.TestResult;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

/**
 * QuantumDMN TCK Test runner that uses the /evaluate/design endpoint.
 * <p>
 * This runner sends DMN models and test inputs to the QuantumDMN API
 * and compares the results with expected values from TCK test cases.
 * </p>
 */
@RunWith(DmnTckSuite.class)
public class QuantumDmnTCKTest implements DmnTckVendorTestSuite {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuantumDmnTCKTest.class);

    private DefaultApi api;

    public QuantumDmnTCKTest() {
        try {
            initializeApi();
        } catch (Exception e) {
            LOGGER.error("Failed to initialize QuantumDMN API client", e);
            throw new RuntimeException("Failed to initialize QuantumDMN API client", e);
        }
    }

    private void initializeApi() throws IOException {
        String baseUrl = ConfigHelper.getBaseUrl();
        String issuer = ConfigHelper.getAuthIssuer();
        String keyFile = ConfigHelper.getKeyFile();
        String projectId = ConfigHelper.getProjectId();

        LOGGER.info("Initializing QuantumDMN API client with base URL: {}", baseUrl);

        if (keyFile != null && !keyFile.isEmpty()) {
            // use Zitadel token provider - constructor is (keyPath, issuer, projectId)
            ZitadelTokenProvider tokenProvider = new ZitadelTokenProvider(keyFile, issuer, projectId);
            DmnService service = new DmnService(baseUrl, tokenProvider);
            this.api = service.getApi();
        } else {
            // use static token from environment
            String token = System.getenv("QUANTUMDMN_TOKEN");
            if (token == null || token.isEmpty()) {
                throw new IllegalStateException("No authentication configured. Set QUANTUMDMN_TOKEN or configure key-file.");
            }
            DmnService service = new DmnService(baseUrl, token);
            this.api = service.getApi();
        }
    }

    @Override
    public List<URL> getTestCases() {
        List<URL> testCases = new ArrayList<>();
        addTestCasesFolders(new File("../../TestCases/compliance-level-2"), testCases);
        addTestCasesFolders(new File("../../TestCases/compliance-level-3"), testCases);
        testCases.sort(Comparator.comparing(URL::toString));
        return testCases;
    }

    private void addTestCasesFolders(File folder, List<URL> testCases) {
        String filter = System.getProperty("dmn.tck.filter");
        if (filter == null || filter.isEmpty()) {
            filter = System.getenv("TCK_FILTER");
        }

        File[] testCasesFolders = folder.listFiles(File::isDirectory);
        if (testCasesFolders != null) {
            for (File file : testCasesFolders) {
                if (filter != null && !filter.isEmpty() && !file.getName().contains(filter)) {
                    continue;
                }
                try {
                    testCases.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    LOGGER.error("Cannot process test file URL: {}", file.toURI(), e);
                }
            }
        }
    }

    @Override
    public TestSuiteContext createContext() {
        LOGGER.info("Creating test context");
        return new QuantumDmnContext();
    }

    @Override
    public void beforeTestCases(TestSuiteContext context, TestCases testCases, URL modelURL) {
        beforeTestCases(context, testCases, modelURL, Collections.emptyList());
    }

    @Override
    public void beforeTestCases(TestSuiteContext context, TestCases testCases, URL modelURL, Collection<? extends URL> additionalModels) {
        LOGGER.info("Loading DMN model: {} with {} additional models", modelURL, additionalModels.size());
        QuantumDmnContext ctx = (QuantumDmnContext) context;

        try {
            // load main DMN file
            String dmnXml = Files.readString(new File(modelURL.getFile()).toPath());
            ctx.setDmnXml(dmnXml);
            ctx.setModelName(testCases.getModelName());
            
            // extract filename and swap extension for filtering
            // e.g. .../0076-feel-external-java-test-01.dmn -> 0076-feel-external-java-test-01.xml
            String fileName = new File(modelURL.getFile()).getName();
            if (fileName.endsWith(".dmn")) {
                fileName = fileName.substring(0, fileName.length() - 4) + ".xml";
            }
            ctx.setDmnFileName(fileName);


            // handle imports
            List<String> additionalXmls = new ArrayList<>();
            for (URL url : additionalModels) {
                try {
                    String xml = Files.readString(new File(url.getFile()).toPath());
                    additionalXmls.add(xml);
                } catch (IOException e) {
                    LOGGER.error("Failed to read additional model: {}", url, e);
                }
            }
            ctx.setAdditionalXmls(additionalXmls);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load DMN model: " + modelURL, e);
        }
    }

    @Override
    public void beforeTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {
        LOGGER.debug("Before test: {} / {}", description.getClassName(), description.getMethodName());
    }

    @Override
    public TestResult executeTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {
        LOGGER.info("Executing test: {} / {}", description.getClassName(), description.getMethodName());
        QuantumDmnContext ctx = (QuantumDmnContext) context;

        try {
            // build input context from test case
            Map<String, FeelValue> inputContext = new LinkedHashMap<>();
            for (TestCases.TestCase.InputNode inputNode : testCase.getInputNode()) {
                FeelValue value = TckValueParser.parse(inputNode);
                inputContext.put(inputNode.getName(), value);
            }

            // determine which decisions to evaluate
            List<String> decisions = new ArrayList<>();
            for (TestCases.TestCase.ResultNode resultNode : testCase.getResultNode()) {
                decisions.add(resultNode.getName());
            }

            // call the evaluate/design endpoint
            EvaluateDesignRequest request = new EvaluateDesignRequest();
            request.setXml(ctx.getDmnXml());
            request.setAdditionalXmls(ctx.getAdditionalXmls());
            request.setContext(inputContext);
            
            // check if this is a decision service direct invocation
            String invocableName = testCase.getInvocableName();
            TestCaseType testType = testCase.getType();
            if (testType == TestCaseType.DECISION_SERVICE && invocableName != null && !invocableName.isEmpty()) {
                // for decision service direct invocation, use the invocableName as the decision service
                request.setDecisionServices(Collections.singletonList(invocableName));
            } else {
                request.setDecisions(decisions);
            }

            Map<String, EvaluationResult> results;
            try {
                results = api.evaluateDesign(request);
            } catch (ApiException e) {
                return TestResult.error("API call failed: " + e.getMessage());
            }

            // compare results
            List<String> failures = new ArrayList<>();
            for (TestCases.TestCase.ResultNode resultNode : testCase.getResultNode()) {
                String testKey = ctx.getDmnFileName() + ":" + resultNode.getName();
                String decisionName = resultNode.getName();
                EvaluationResult actualResult = results.get(decisionName);

                if (actualResult == null) {
                    failures.add("Missing result for decision: " + decisionName);
                    continue;
                }

                // parse expected value early to check for null expectation
                FeelValue expectedValue = TckValueParser.parse(resultNode.getExpected());
                boolean expectsNull = expectedValue == null || expectedValue.isNull();

                // handle errorResult cases
                if (resultNode.isErrorResult()) {
                    // expect an error
                    String error = actualResult.getError();
                    if (error == null || error.isEmpty()) {
                        FeelValue actualValue = actualResult.getValue();
                        // if we got a value, but expected error
                        if (actualValue == null || actualValue.isNull()) {
                             // OK
                        } else {
                            failures.add("Expected error for '" + testKey + "' but got value: " + String.valueOf(actualValue));
                        }
                    }
                    continue;
                }

                // api sends error even if the result is correct
                String error = actualResult.getError();
                if (error != null && !error.isEmpty()) {
                    // if we have an error, but we EXPECT null, we can accept the error as "result is null" the engine provides error information for some null results
                    if (expectsNull) {
                        LOGGER.warn("Ignoring error for '{}' because expected value is null: error='{}'", testKey, error);
                        continue;
                    } else {
                        LOGGER.warn("Unexpected error for '{}'. expectedValue='{}', expectsNull='{}', error='{}'", testKey, expectedValue, expectsNull, error);
                    }

                    FeelValue actualValue = actualResult.getValue();
                    if (FeelValueComparator.areEqual(expectedValue, actualValue)) {
                       LOGGER.warn("Ignoring unexpected error for '{}' because values match: error='{}', value='{}'", testKey, error, actualValue);
                       continue;
                    }
                    failures.add("Unexpected error for '" + testKey + "': " + error);
                    continue;
                }

                // compare expected vs actual value
                FeelValue actualValue = actualResult.getValue();

                if (!FeelValueComparator.areEqual(expectedValue, actualValue)) {
                    failures.add("Mismatch for '" + testKey + "': expected=" + String.valueOf(expectedValue) + ", actual=" + String.valueOf(actualValue));
                }
            }

            if (!failures.isEmpty()) {
                String msg = String.join("; ", failures);
                return TestResult.error(escapeCsv(msg));
            }

            return TestResult.success("");

        } catch (Exception e) {
            LOGGER.error("Error executing test: {} / {}", description.getClassName(), description.getMethodName(), e);
            return TestResult.error("Exception: " + escapeCsv(e.getMessage()));
        }
    }

    @Override
    public void afterTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {
        LOGGER.debug("After test: {} / {}", description.getClassName(), description.getMethodName());
    }

    @Override
    public void afterTestCase(TestSuiteContext context, TestCases testCases) {
        LOGGER.debug("After test case: {}", testCases.getModelName());
    }

    @Override
    public String getResultFileName() {
        return "target/tck_results.csv";
    }

    private String escapeCsv(String text) {
        if (text == null) {
            return "null";
        }
        return text.replace("\"", "'").replace("\n", "").replace("\r", "");
    }
}
