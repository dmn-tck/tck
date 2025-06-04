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

package org.omg.dmn.tck.runner.jdmn;

import com.gs.dmn.log.Slf4jBuildLogger;
import com.gs.dmn.tck.TCKSerializer;
import com.gs.dmn.tck.ast.TestCase;
import com.gs.dmn.tck.ast.TestCases;
import com.gs.dmn.tck.serialization.xstream.XMLTCKSerializer;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.omg.dmn.tck.runner.junit4.TestResult;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static org.omg.dmn.tck.runner.jdmn.JDMNTestContext.getInputParameters;

public class JDmnTckRunner extends ParentRunner<TestCase> {
    private static final Logger logger = LoggerFactory.getLogger(JDmnTckRunner.class);

    // The description of the test suite
    private final ConcurrentMap<TestCase, Description> children = new ConcurrentHashMap<>();
    private final JDmnTckVendorTestSuite vendorSuite;
    private Description descr;
    private TestSuiteContext context;
    private TestCases tcd;
    private URL modelURL;
    private Collection<URL> additionalModels = new ArrayList<>();
    private FileWriter resultFile;
    private String folder = "<unknown>";
    private TCKSerializer serializer = new XMLTCKSerializer(new Slf4jBuildLogger(logger), getInputParameters());
    public JDmnTckRunner(JDmnTckVendorTestSuite vendorSuite, File tcfile)
            throws InitializationError {
        super(vendorSuite.getClass());
        this.vendorSuite = vendorSuite;
        try {
            tcd = serializer.read(tcfile);
            String parent = tcfile.getParent();
            modelURL = new File(parent != null ? parent + "/" + tcd.getModelName() : tcd.getModelName()).toURI().toURL();
            if (Paths.get(parent) != null) {
                try {
                    List<File> allDMNFiles = Files.walk(Paths.get(parent))
                            .map(Path::toFile)
                            .filter(File::isFile)
                            .filter(f -> f.getName().endsWith(".dmn"))
                            .collect(Collectors.toList());
                    for (File f : allDMNFiles) {
                        try {
                            additionalModels.add(f.toURI().toURL());
                        } catch (Throwable e) {
                            e.printStackTrace(); //and continue to next file
                        }
                    }
                    additionalModels.remove(modelURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            folder = parent != null ? parent.substring(parent.lastIndexOf('/', parent.lastIndexOf('/') - 1) + 1) : "<unknown>";
            String tcdname = tcfile.getName();
            tcdname = tcdname.substring(0, tcdname.lastIndexOf('.')).replaceAll("\\.", "/");
            this.descr = Description.createSuiteDescription(tcdname);
            for (TestCase test : tcd.getTestCase()) {
                Description testDescr = Description.createTestDescription(tcdname, test.getId());
                children.put(test, testDescr);
                this.descr.addChild(testDescr);
            }
            File results = new File(vendorSuite.getResultFileName());
            if (results.exists()) {
                results.delete();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<TestCase> getChildren() {
        ArrayList<TestCase> testCases = new ArrayList<>(children.keySet());
        Collections.sort(testCases, (o1, o2) -> o1.getId().compareTo(o2.getId()));
        return testCases;
    }

    @Override
    protected Description describeChild(TestCase testCases) {
        return this.children.get(testCases);
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            String resultFilePath = vendorSuite.getResultFileName();
            if (resultFilePath != null) {
                resultFile = new FileWriter(resultFilePath, true);
            } else {
                logger.error("Result file path is null. Skipping result file generation.");
            }
            try {
                context = vendorSuite.createContext();
            } catch (Throwable e) {
                logger.error("Error creating context for model " + modelURL, e);
                notifier.fireTestFailure(new Failure(descr, e));
                context = null;
                return;
            }
            try {
                vendorSuite.beforeTestCases(context, tcd, modelURL, additionalModels);
            } catch (Throwable e) {
                logger.error("Error running 'beforeTestCases()' for model " + modelURL, e);
                notifier.fireTestFailure(new Failure(descr, e));
                context = null;
                return;
            }
            try {
                super.run(notifier);
            } catch (Throwable e) {
                logger.error("Error running test cases for model " + modelURL, e);
            }
            try {
                vendorSuite.afterTestCase(context, tcd);
            } catch (Throwable e) {
                logger.error("Error running test cases for model " + modelURL, e);
                return;
            } finally {
                context = null;
            }
        } catch (Throwable t) {
            logger.error("Unable to open results CSV file", t);
        } finally {
            if (resultFile != null) {
                try {
                    resultFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void runChild(TestCase testCase, RunNotifier runNotifier) {
        Description description = this.children.get(testCase);
        try {
            runNotifier.fireTestStarted(description);
            vendorSuite.beforeTest(description, context, testCase);
            TestResult result = vendorSuite.executeTest(description, context, testCase);
            switch (result.getResult()) {
                case SUCCESS:
                    runNotifier.fireTestFinished(description);
                    break;
                case IGNORED:
                    runNotifier.fireTestIgnored(description);
                    break;
                case ERROR:
                    runNotifier.fireTestFailure(new Failure(description, new RuntimeException(result.toStringWithLines())));
                    break;
            }
            if (resultFile != null) {
                String relativePath = relativePath(folder);
                resultFile.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n", relativePath, description.getClassName(), description.getMethodName(),
                        result.getResult().toString(), result.getMsg()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            vendorSuite.afterTest(description, context, testCase);
            runNotifier.fireTestFinished(description);
        }
    }

    @Override
    public Description getDescription() {
        return descr;
    }

    private String relativePath(String path) {
        String[] pathParts = path.replace('\\', '/').split("/");
        int len = pathParts.length;
        return String.format("%s/%s", pathParts[len - 2], pathParts[len - 1]);
    }
}