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
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.core.api.*;
import org.kie.dmn.core.ast.InputDataNode;
import org.kie.internal.utils.KieHelper;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.runner.junit4.DmnTckSuite;
import org.omg.dmn.tck.runner.junit4.DmnTckVendorTestSuite;
import org.omg.dmn.tck.runner.junit4.TestResult;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith( DmnTckSuite.class )
public class DroolsTCKTest
        implements DmnTckVendorTestSuite {

    private static final Logger logger = LoggerFactory.getLogger( DroolsTCKTest.class );

    public List<URL> getTestCases() {
        List<URL> testCases = new ArrayList<>(  );
        File cl2parent = new File("../../TestCases/compliance-level-2");
        FilenameFilter filenameFilter = (dir, name) -> name.matches( "\\d\\d\\d\\d-.*" );
        for( File file : cl2parent.listFiles( filenameFilter ) ) {
            try {
                testCases.add( file.toURI().toURL() );
            } catch ( MalformedURLException e ) {
                e.printStackTrace();
            }
        }
        File cl3parent = new File("../../TestCases/compliance-level-3");
        for( File file : cl3parent.listFiles( filenameFilter ) ) {
            try {
                testCases.add( file.toURI().toURL() );
            } catch ( MalformedURLException e ) {
                e.printStackTrace();
            }
        }
        return testCases;
    }

    public TestSuiteContext createContext() {
        logger.info( "Creating context.");
        return new DroolsContext();
    }

    public void beforeTestCases(TestSuiteContext context, TestCases testCases, URL modelURL ) {
        logger.info( "Creating runtime for model: "+modelURL );
        DroolsContext ctx = (DroolsContext)context;
        ctx.runtime = createRuntime( modelURL );
        ctx.dmnmodel = ctx.runtime.getModels().get( 0 );
    }

    public void beforeTest(TestSuiteContext context, TestCases.TestCase testCase) {
        // nothing to do
    }

    public TestResult executeTest(TestSuiteContext context, TestCases.TestCase testCase) {
        logger.info( "Executing test {}", testCase.getId() );
        DroolsContext ctx = (DroolsContext)context;

        DMNContext dmnctx = DMNFactory.newContext();
        testCase.getInputNode().forEach( in -> {
            InputDataNode input = ctx.dmnmodel.getInputByName( in.getName() );
            dmnctx.set( in.getName(), parseValue( in, input ) );
        } );

        DMNResult dmnResult = ctx.runtime.evaluateAll( ctx.dmnmodel, dmnctx );
        logger.info( dmnResult.getContext().toString() );

        testCase.getResultNode().forEach( rn -> {
            String name = rn.getName();
            String expected = ((Node)rn.getComputed().getValue()).getFirstChild().getTextContent();
            Object resultObject = dmnResult.getContext().get( name );
            // this needs to improve to take into account the actual result type
            String resultString = resultObject != null ? resultObject.toString() : null;
            assertThat( resultString, is( expected ) );
        } );

        TestResult.Result r = dmnResult.hasErrors() ? TestResult.Result.ERROR : TestResult.Result.SUCCESS;
        return new TestResult( r, "Yey" );
    }

    public void afterTest(TestSuiteContext context, TestCases.TestCase testCase) {
        // nothing to do
    }

    public void afterTestCase(TestSuiteContext context, TestCases testCases) {
        // nothing to do
    }

    protected DMNRuntime createRuntime( URL modelUrl ) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kieContainer = new KieHelper(  )
                .addResource( ks.getResources().newUrlResource( modelUrl ) )
                .getKieContainer();

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime( DMNRuntime.class );
        assertNotNull( runtime );
        return runtime;
    }

    private Object parseValue( TestCases.TestCase.InputNode in, InputDataNode input ) {
        String text = ((Node)in.getValue()).getFirstChild().getTextContent();
        return text != null ? input.getDmnType().parseValue( text ) : null;
    }

    public static class DroolsContext implements TestSuiteContext {
        public DMNRuntime runtime;
        public DMNModel   dmnmodel;
    }



}
