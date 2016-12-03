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

import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.core.api.*;
import org.kie.dmn.core.ast.DecisionNode;
import org.kie.dmn.core.ast.InputDataNode;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.internal.utils.KieHelper;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.marshaller._20160719.ValueType;
import org.omg.dmn.tck.runner.junit4.DmnTckSuite;
import org.omg.dmn.tck.runner.junit4.DmnTckVendorTestSuite;
import org.omg.dmn.tck.runner.junit4.TestResult;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith( DmnTckSuite.class )
public class DroolsTCKTest
        implements DmnTckVendorTestSuite {

    private static final Logger logger = LoggerFactory.getLogger( DroolsTCKTest.class );
    public static final BigDecimal NUMBER_COMPARISON_PRECISION = new BigDecimal( "0.00000001" );

    public List<URL> getTestCases() {
        List<URL> testCases = new ArrayList<>(  );
        File cl2parent = new File("../../TestCases/compliance-level-2");
        FilenameFilter filenameFilter = (dir, name) -> name.matches( "\\d\\d\\d\\d-.*" );
//        FilenameFilter filenameFilter = (dir, name) -> name.matches( "0010-.*" );
//        for( File file : cl2parent.listFiles( filenameFilter ) ) {
//            try {
//                testCases.add( file.toURI().toURL() );
//            } catch ( MalformedURLException e ) {
//                e.printStackTrace();
//            }
//        }
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

    public void beforeTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {
        // nothing to do
    }

    public TestResult executeTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {
        DroolsContext ctx = (DroolsContext)context;
        logger.info( "Executing test '{} / {}'", description.getClassName(), description.getMethodName() );

        DMNContext dmnctx = DMNFactory.newContext();
        testCase.getInputNode().forEach( in -> {
            InputDataNode input = ctx.dmnmodel.getInputByName( in.getName() );
            dmnctx.set( in.getName(), parseValue( in, input ) );
        } );

        DMNResult dmnResult = ctx.runtime.evaluateAll( ctx.dmnmodel, dmnctx );
        logger.info( dmnResult.getContext().toString() );

        List<String> failures = new ArrayList<>();
        if( dmnResult.hasErrors() ) {
            for( DMNMessage msg : dmnResult.getMessages( DMNMessage.Severity.ERROR ) ) {
                failures.add( msg.toString() );
            }
        }

        testCase.getResultNode().forEach( rn -> {
            try {
                String name = rn.getName();
                Object expected = parseValue( rn, ctx.dmnmodel.getDecisionByName( name ) );
                Object actual = dmnResult.getContext().get( name );
                if( ! isEquals( expected, actual ) ) {
                    failures.add( "FAILURE: '"+name+"' expected='"+expected+"' but found='"+actual+"'" );
                }
            } catch ( Throwable t ) {
                failures.add( "FAILURE: unnexpected exception executing test case '"+description.getClassName()+" / " +description.getMethodName()+"': "+t.getClass().getName() );
                logger.error( "FAILURE: unnexpected exception executing test case '{} / {}'", description.getClassName(), description.getMethodName(), t );
            }
        } );

        TestResult.Result r = dmnResult.hasErrors() || !failures.isEmpty() ? TestResult.Result.ERROR : TestResult.Result.SUCCESS;
        return new TestResult( r, failures.toString() );
    }

    private boolean isEquals( Object expected, Object actual ) {
        if( expected == actual ) {
            // this includes both being null
            return true;
        }
        if( ( expected == null && actual != null ) ||
            ( expected != null && actual == null ) ) {
            return false;
        }
        if( expected instanceof Number && actual instanceof Number ) {
            BigDecimal expectedBD = EvalHelper.getBigDecimalOrNull( expected );
            BigDecimal actualBD = EvalHelper.getBigDecimalOrNull( actual );
            return expectedBD.subtract( actualBD ).abs().compareTo( NUMBER_COMPARISON_PRECISION ) < 0;
        }
        if( ! expected.getClass().isAssignableFrom( actual.getClass() ) ) {
            return false;
        }
        return expected.equals( actual );
    }

    public void afterTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {
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
        if( runtime == null ) {
            throw new RuntimeException( "Unable to create DMN Runtime" );
        }
        return runtime;
    }

    private Object parseValue( TestCases.TestCase.InputNode in, InputDataNode input ) {
        if( input == null || input.getDmnType() == null ) {
            throw new RuntimeException( "Unknown type for input node "+in.getName() );
        }
        return parseType( in, input.getDmnType() );
    }

    private Object parseValue( TestCases.TestCase.ResultNode rn, DecisionNode decision ) {
        if( decision == null || decision.getResultType() == null ) {
            throw new RuntimeException( "Unknown type for input node "+rn.getName() );
        }
        return parseType( rn.getExpected(), decision.getResultType() );
    }

    private Object parseType(ValueType value, DMNType dmnType) {
        if( ! dmnType.isComposite() ) {
            String text = ((Node)value.getValue()).getFirstChild().getTextContent();
            return text != null ? dmnType.parseValue( text ) : null;
        } else{
            Map<String, Object> result = new HashMap<>();
            for ( ValueType.Component component : value.getComponent() ) {
                DMNType fieldType = dmnType.getField( component.getName() );
                if( fieldType == null ) {
                    throw new RuntimeException( "Error parsing input: unknown field type for field: "+component.getName() );
                }
                Object fieldValue = parseType( component, fieldType );
                result.put( component.getName(), fieldValue );
            }
            return result;
        }
    }

    public static class DroolsContext implements TestSuiteContext {
        public DMNRuntime runtime;
        public DMNModel   dmnmodel;
    }



}
