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
import com.gs.dmn.dialect.DMNDialectDefinition;
import com.gs.dmn.feel.analysis.semantics.environment.DefaultDMNEnvironmentFactory;
import com.gs.dmn.feel.analysis.semantics.environment.EnvironmentFactory;
import com.gs.dmn.feel.lib.StandardFEELLib;
import com.gs.dmn.log.BuildLogger;
import com.gs.dmn.log.NopBuildLogger;
import com.gs.dmn.runtime.Pair;
import com.gs.dmn.runtime.interpreter.DMNInterpreter;
import com.gs.dmn.serialization.DMNNamespacePrefixMapper;
import com.gs.dmn.serialization.DMNReader;
import com.gs.dmn.serialization.DMNWriter;
import com.gs.dmn.serialization.PrefixNamespaceMappings;
import com.gs.dmn.tck.TestCasesReader;
import com.gs.dmn.transformation.DMNTransformer;
import com.gs.dmn.transformation.ToQuotedNameTransformer;
import com.gs.dmn.transformation.ToSimpleNameTransformer;
import com.gs.dmn.transformation.basic.BasicDMN2JavaTransformer;
import com.gs.dmn.transformation.lazy.NopLazyEvaluationDetector;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;
import org.omg.spec.dmn._20180521.model.TDefinitions;

import java.io.File;
import java.net.URL;
import java.util.*;

public class JDMNTestContext implements TestSuiteContext {
    private static final boolean DEBUG_TRANSFORMER = false;

    private final DMNReader dmnReader;
    private final DMNWriter dmnWriter;
    private final TestCasesReader tckReader;
    private final DMNTransformer<TestCases> dmnTransformer;
    private final DMNDialectDefinition dialectDefinition;
    private final StandardFEELLib lib;
    private final EnvironmentFactory environmentFactory;

    private DMNInterpreter interpreter;
    private BasicDMN2JavaTransformer basicToJavaTransformer;
    public JDMNTestContext(DMNReader dmnReader, DMNWriter dmnWriter, DMNTransformer<TestCases> dmnTransformer, DMNDialectDefinition dialectDefinition) {
        this.dmnReader = dmnReader;
        this.dmnWriter = dmnWriter;
        this.tckReader = new TestCasesReader(new NopBuildLogger());
        this.dmnTransformer = dmnTransformer;
        this.dialectDefinition = dialectDefinition;
        this.lib = (StandardFEELLib) dialectDefinition.createFEELLib();
        this.environmentFactory = DefaultDMNEnvironmentFactory.instance();
    }

    public DMNInterpreter getInterpreter() {
        return interpreter;
    }

    public StandardFEELLib getLib() {
        return lib;
    }

    public BasicDMN2JavaTransformer getBasicToJavaTransformer() {
        return basicToJavaTransformer;
    }

    public EnvironmentFactory getEnvironmentFactory() {
        return environmentFactory;
    }

    public void prepareModel(URL modelURL, Collection<? extends URL> additionalModelURLs, BuildLogger logger) {
        Map<URL, TDefinitions> modelMap = new LinkedHashMap<>();
        if (modelURL != null) {
            Pair<TDefinitions, PrefixNamespaceMappings> pair = dmnReader.read(modelURL);
            modelMap.put(modelURL, pair.getLeft());
        }
        for (URL url: additionalModelURLs) {
            Pair<TDefinitions, PrefixNamespaceMappings> pair = dmnReader.read(url);
            modelMap.put(url, pair.getLeft());
        }
        DMNModelRepository repository = new DMNModelRepository(new ArrayList<>(modelMap.values()), new PrefixNamespaceMappings());
        this.dmnTransformer.transform(repository);
        if (DEBUG_TRANSFORMER) {
            for (Map.Entry<URL, TDefinitions> entry: modelMap.entrySet()) {
                save(entry.getKey(), entry.getValue());
            }
        }
        this.interpreter = dialectDefinition.createDMNInterpreter(repository);
        this.basicToJavaTransformer = dialectDefinition.createBasicTransformer(repository, new NopLazyEvaluationDetector(), new LinkedHashMap<>());
    }

    public void clean(TestCases testCases) {
        DMNModelRepository repository = basicToJavaTransformer.getDMNModelRepository();
        this.dmnTransformer.transform(repository, testCases);
        if (DEBUG_TRANSFORMER) {
            save(testCases);
        }
    }

    private static Map<Object, String> TEMP_FOLDER = new LinkedHashMap<>();
    static {
        TEMP_FOLDER.put(ToSimpleNameTransformer.class, "target/to-java-name/");
        TEMP_FOLDER.put(ToQuotedNameTransformer.class, "target/to-quoted-name/");
    }

    private void save(URL modelURL, TDefinitions definitions) {
        String[] parts = modelURL.toString().split("/");
        String modelName = parts[parts.length-1];
        String tempFolder = TEMP_FOLDER.get(dmnTransformer.getClass());
        new File(tempFolder).mkdirs();
        File dmnFile = new File(tempFolder + modelName);
        this.dmnWriter.write(definitions, dmnFile, new DMNNamespacePrefixMapper());
    }

    private void save(TestCases testCases) {
        String tempFolder = TEMP_FOLDER.get(dmnTransformer.getClass());
        File tckFile = new File(tempFolder + testCases.getModelName().replace(".dmn", "") + "-test-01.xml");
        this.tckReader.write(testCases, tckFile, new DMNNamespacePrefixMapper());
    }
}
