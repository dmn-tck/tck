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

package org.omg.dmn.tck.runner.trisotech.junit4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.omg.dmn.tck.marshaller.TckMarshallingHelper;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.marshaller._20160719.TestCases.TestCase;
import org.omg.dmn.tck.runner.trisotech.TrisotechTCKHelper;
import org.xml.sax.SAXException;

public class TrisotechTCKTestRunner extends ParentRunner<TrisotechTCKTest> {

    private final Properties properties;

    private final String category;

    private final String testId;

    private final File testCaseFile;

    private final List<TrisotechTCKTest> testCases = new LinkedList<TrisotechTCKTest>();

    private final Description runnerDescription;

    private final String testCase;

    private final String modelName;

    private final File resultCSV;

    protected TrisotechTCKTestRunner(Properties properties, Class<TrisotechTCKTest> testClass, String category, String testId, String testCase,
            File testCaseFile, File resultCSV) throws InitializationError {
        super(testClass);
        this.properties = properties;
        this.category = category;
        this.testId = testId;
        this.testCase = testCase;
        this.testCaseFile = testCaseFile;
        this.resultCSV = resultCSV;
        runnerDescription = Description.createSuiteDescription(category + " - " + testCase);
        try {
            TestCases tcs = TckMarshallingHelper.load(new FileInputStream(testCaseFile));
            modelName = tcs.getModelName();
            for (TestCase tc : tcs.getTestCase()) {
                Description testDescription = Description.createTestDescription(testClass, testCase + " - " + tc.getId());
                TrisotechTCKTest myTest = testClass.newInstance();
                myTest.setId(tc.getId());
                myTest.setDescription(testDescription);
                testCases.add(myTest);
                runnerDescription.addChild(testDescription);
            }
        } catch (FileNotFoundException | JAXBException | InstantiationException | IllegalAccessException e) {
            throw new InitializationError(e);
        }

    }

    @Override
    public Description getDescription() {
        return runnerDescription;
    }

    @Override
    protected List<TrisotechTCKTest> getChildren() {
        return testCases;
    }

    @Override
    protected void runChild(TrisotechTCKTest test, RunNotifier notifier) {
        try (FileWriter resultFile = new FileWriter(resultCSV, true)) {
            notifier.fireTestStarted(test.getDescription());
            if (test.getPassed()) {
                notifier.fireTestFinished(test.getDescription());
                resultFile.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n", category + "/" + testId, testCase, test.getId(), "SUCCESS", ""));
            } else {
                notifier.fireTestFailure(new Failure(test.getDescription(), test.getException()));
                resultFile.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n", category + "/" + testId, testCase, test.getId(), "ERROR", ""));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Description describeChild(TrisotechTCKTest child) {
        return child.getDescription();
    }

    @Override
    public void run(RunNotifier notifier) {
        Map<String, Boolean> results = null;
        try {
            if (TrisotechTCKHelper.pushTestCase(category, testId, new File(testCaseFile.getParentFile(), modelName), properties)) {
                results = TrisotechTCKHelper.runTestCase(category, testId, testCaseFile, properties);
            } else {
                for (TrisotechTCKTest test : testCases) {
                    test.setException(new Exception("Could not deploy model to the cloud"));
                }
            }
        } catch (IOException | SAXException e) {
            for (TrisotechTCKTest test : testCases) {
                test.setException(e);
            }

        }
        if (results != null) {
            for (TrisotechTCKTest test : testCases) {
                Boolean result = results.get(test.getId());
                if (result != null) {
                    test.setPassed(result);
                }
            }
        }
        super.run(notifier);
    }

}
