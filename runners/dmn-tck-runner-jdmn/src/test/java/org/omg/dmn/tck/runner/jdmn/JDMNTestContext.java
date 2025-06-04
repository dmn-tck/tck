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

import com.gs.dmn.DMNModelRepository;
import com.gs.dmn.ast.TDefinitions;
import com.gs.dmn.context.environment.EnvironmentFactory;
import com.gs.dmn.dialect.DMNDialectDefinition;
import com.gs.dmn.feel.analysis.semantics.environment.StandardEnvironmentFactory;
import com.gs.dmn.feel.lib.StandardFEELLib;
import com.gs.dmn.log.BuildLogger;
import com.gs.dmn.log.NopBuildLogger;
import com.gs.dmn.runtime.interpreter.DMNInterpreter;
import com.gs.dmn.serialization.DMNSerializer;
import com.gs.dmn.tck.TCKSerializer;
import com.gs.dmn.tck.ast.TestCases;
import com.gs.dmn.tck.serialization.xstream.XMLTCKSerializer;
import com.gs.dmn.transformation.DMNTransformer;
import com.gs.dmn.transformation.InputParameters;
import com.gs.dmn.transformation.ToQuotedNameTransformer;
import com.gs.dmn.transformation.basic.BasicDMNToJavaTransformer;
import com.gs.dmn.transformation.lazy.NopLazyEvaluationDetector;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;

import java.io.File;
import java.net.URL;
import java.util.*;

public class JDMNTestContext<NUMBER, DATE, TIME, DATE_TIME, DURATION> implements TestSuiteContext {
    private static final Map<String, String> INPUT_PARAMETERS_MAP = new LinkedHashMap<>() {{
        put("xsdValidation", "true");
        put("singletonInputData", "true");
        put("strongTyping", "false");
    }};
    private static InputParameters INPUT_PARAMETERS = new InputParameters(INPUT_PARAMETERS_MAP);
    public static InputParameters getInputParameters() {
        return INPUT_PARAMETERS;
    }

    private static final boolean DEBUG_TRANSFORMER = false;

    private final DMNSerializer dmnSerializer;
    private final TCKSerializer tckSerializer;
    private final DMNTransformer<TestCases> dmnTransformer;
    private final DMNDialectDefinition<NUMBER, DATE, TIME, DATE_TIME, DURATION, TestCases> dialectDefinition;
    private final StandardFEELLib<NUMBER, DATE, TIME, DATE_TIME, DURATION> lib;
    private final EnvironmentFactory environmentFactory;

    private DMNInterpreter<NUMBER, DATE, TIME, DATE_TIME, DURATION> interpreter;
    private BasicDMNToJavaTransformer basicToJavaTransformer;
    private TestCases testCases;

    public JDMNTestContext(DMNSerializer dmnSerializer, DMNTransformer<TestCases> dmnTransformer, DMNDialectDefinition<NUMBER, DATE, TIME, DATE_TIME, DURATION, TestCases> dialectDefinition) {
        this.dmnSerializer = dmnSerializer;
        this.tckSerializer = new XMLTCKSerializer(new NopBuildLogger(), getInputParameters());
        this.dmnTransformer = dmnTransformer;
        this.dialectDefinition = dialectDefinition;
        this.lib = (StandardFEELLib<NUMBER, DATE, TIME, DATE_TIME, DURATION>) dialectDefinition.createFEELLib();
        this.environmentFactory = StandardEnvironmentFactory.instance();
    }

    public DMNInterpreter<NUMBER, DATE, TIME, DATE_TIME, DURATION> getInterpreter() {
        return interpreter;
    }

    public StandardFEELLib<NUMBER, DATE, TIME, DATE_TIME, DURATION> getLib() {
        return lib;
    }

    public BasicDMNToJavaTransformer getBasicToJavaTransformer() {
        return basicToJavaTransformer;
    }

    public EnvironmentFactory getEnvironmentFactory() {
        return environmentFactory;
    }

    public void prepareModel(URL modelURL, Collection<? extends URL> additionalModelURLs, BuildLogger logger) {
        Map<URL, TDefinitions> modelMap = new LinkedHashMap<>();
        List<TDefinitions> pairs = new ArrayList<>();
        if (modelURL != null) {
            File input = new File(modelURL.getPath());
            TDefinitions definitions = dmnSerializer.readModel(input);
            modelMap.put(modelURL, definitions);
            pairs.add(definitions);
        }
        for (URL url: additionalModelURLs) {
            File input = new File(url.getPath());
            TDefinitions definitions = dmnSerializer.readModel(input);
            modelMap.put(modelURL, definitions);
            pairs.add(definitions);
        }
        DMNModelRepository repository = new DMNModelRepository(pairs);
        this.dmnTransformer.transform(repository);
        if (DEBUG_TRANSFORMER) {
            for (Map.Entry<URL, TDefinitions> entry: modelMap.entrySet()) {
                save(entry.getKey(), entry.getValue());
            }
        }
        InputParameters inputParameters = getInputParameters();
        this.interpreter = dialectDefinition.createDMNInterpreter(repository, inputParameters);
        this.basicToJavaTransformer = dialectDefinition.createBasicTransformer(repository, new NopLazyEvaluationDetector(), inputParameters);
    }

    public void clean(TestCases testCases) {
        DMNModelRepository repository = basicToJavaTransformer.getDMNModelRepository();
        this.dmnTransformer.transform(repository, Arrays.asList(testCases));
        if (DEBUG_TRANSFORMER) {
            save(testCases);
        }
    }

    private static Map<Object, String> TEMP_FOLDER = new LinkedHashMap<>();
    static {
        TEMP_FOLDER.put(ToQuotedNameTransformer.class, "target/to-quoted-name/");
    }

    private void save(URL modelURL, TDefinitions definitions) {
        String[] parts = modelURL.toString().split("/");
        String modelName = parts[parts.length-1];
        String tempFolder = TEMP_FOLDER.get(dmnTransformer.getClass());
        new File(tempFolder).mkdirs();
        File dmnFile = new File(tempFolder + modelName);
        this.dmnSerializer.writeModel(definitions, dmnFile);
    }

    private void save(TestCases testCases) {
        String tempFolder = TEMP_FOLDER.get(dmnTransformer.getClass());
        File tckFile = new File(tempFolder + testCases.getModelName().replace(".dmn", "") + "-test-01.xml");
        this.tckSerializer.write(testCases, tckFile);
    }

    public void setTestCases(TestCases testCases) {
        this.testCases = testCases;
    }

    public TestCases getTestCases() {
        return testCases;
    }
}
