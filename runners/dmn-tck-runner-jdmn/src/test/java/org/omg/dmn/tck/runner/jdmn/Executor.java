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

import com.gs.dmn.runtime.compiler.DMNToJavaTranslator;
import com.gs.dmn.runtime.compiler.DMNToJavaTranslatorBuilder;
import com.gs.dmn.runtime.compiler.InMemoryTestCasesExecutor;
import com.gs.dmn.runtime.compiler.TestRunResult;
import com.gs.dmn.transformation.ToQuotedNameTransformer;
import com.gs.dmn.transformation.repository.FileOutputRepository;
import com.gs.dmn.transformation.repository.InputRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.omg.dmn.tck.runner.jdmn.JDMNTckTest.CL3_FOLDER;

public class Executor {
    private static final Logger logger = LoggerFactory.getLogger(Executor.class);

    public TestRunResult execute(File inputFolder, File outputFolder) throws Exception {
        DMNToJavaTranslatorBuilder dmnToJavaTranslatorBuilder = new DMNToJavaTranslatorBuilder();
        dmnToJavaTranslatorBuilder.withDMNTransformer(new ToQuotedNameTransformer());
        InputRepository modelRepository = new InputRepository(inputFolder);
        DMNToJavaTranslator translator = new DMNToJavaTranslator(dmnToJavaTranslatorBuilder.buildDMNTranslator(), dmnToJavaTranslatorBuilder.buildTestCasesTranslator(modelRepository));
        InMemoryTestCasesExecutor executor = new InMemoryTestCasesExecutor(translator);
        TestRunResult results = executor.execute(modelRepository, new FileOutputRepository(outputFolder));
        return results;
    }

    public static void main(String[] args) throws Exception {
        String testName = "0020-vacation-days";
        File testFolder = new File(CL3_FOLDER.getPath(), testName);
        File outputFolder = new File("runners/dmn-tck-runner-jdmn/target/" + testName);
        TestRunResult result = new Executor().execute(testFolder, outputFolder);

        System.out.println("TestsFound " + result.getTestsFound());
        System.out.println("TestsSucceeded " + result.getTestsSucceeded());
        System.out.println("TestsFailed " + result.getTestsFailed());
        System.out.println("TestsAborted " + result.getTestsAborted());
        System.out.println("TestsSkipped " + result.getTestsSkipped());
    }
}