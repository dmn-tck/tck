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

package org.omg.dmn.tck.runner.junit4;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.omg.dmn.tck.marshaller.TckMarshallingHelper;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DmnTckRunner
        extends ParentRunner<TestCases.TestCase> {

    private static final Logger logger = LoggerFactory.getLogger( DmnTckRunner.class );

    // The description of the test suite
    private final ConcurrentMap<TestCases.TestCase, Description> children = new ConcurrentHashMap<>();
    private final DmnTckVendorTestSuite vendorSuite;
    private       Description           descr;
    private       TestSuiteContext      context;
    private       TestCases             tcd;
    private       URL                   modelURL;
    private       FileWriter            resultFile;
    private       String                folder = "<unknown>";

    public DmnTckRunner(DmnTckVendorTestSuite vendorSuite, File tcfile)
            throws InitializationError {
        super( vendorSuite.getClass() );
        this.vendorSuite = vendorSuite;
        try {
            tcd = TckMarshallingHelper.load( new FileInputStream( tcfile ) );
            String parent = tcfile.getParent();
            modelURL = new File( parent != null ? parent + "/" + tcd.getModelName() : tcd.getModelName() ).toURI().toURL();
            folder = parent != null ? parent.substring( parent.lastIndexOf( '/', parent.lastIndexOf( '/' ) - 1 ) + 1 ) : "<unknown>";
            String tcdname = tcfile.getName();
            tcdname = tcdname.substring( 0, tcdname.lastIndexOf( '.' ) ).replaceAll( "\\.", "/" );
            this.descr = Description.createSuiteDescription( tcdname );
            for ( TestCases.TestCase test : tcd.getTestCase() ) {
                Description testDescr = Description.createTestDescription( tcdname, test.getId() );
                children.put( test, testDescr );
                this.descr.addChild( testDescr );
            }
            File results = new File( "tck_results.csv" );
            if ( results.exists() ) {
                results.delete();
            }
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        } catch ( JAXBException e ) {
            e.printStackTrace();
        } catch ( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<TestCases.TestCase> getChildren() {
        return new ArrayList<>( children.keySet() );
    }

    @Override
    protected Description describeChild(TestCases.TestCase testCases) {
        return this.children.get( testCases );
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            resultFile = new FileWriter( "result.csv", true );
            try {
                context = vendorSuite.createContext();
            } catch ( Throwable e ) {
                logger.error( "Error creating context for model " + modelURL, e );
                notifier.fireTestFailure( new Failure( descr, e ) );
                context = null;
                return;
            }
            try {
                vendorSuite.beforeTestCases( context, tcd, modelURL );
            } catch ( Throwable e ) {
                logger.error( "Error running 'beforeTestCases()' for model " + modelURL, e );
                notifier.fireTestFailure( new Failure( descr, e ) );
                context = null;
                return;
            }
            try {
                super.run( notifier );
            } catch ( Throwable e ) {
                logger.error( "Error running test cases for model " + modelURL, e );
            }
            try {
                vendorSuite.afterTestCase( context, tcd );
            } catch ( Throwable e ) {
                logger.error( "Error running test cases for model " + modelURL, e );
                return;
            } finally {
                context = null;
            }
        } catch ( Throwable t ) {
            logger.error( "Unable to open results CSV file", t );
        } finally {
            if ( resultFile != null ) {
                try {
                    resultFile.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void runChild(TestCases.TestCase testCase, RunNotifier runNotifier) {
        Description description = this.children.get( testCase );
        try {
            runNotifier.fireTestStarted( description );
            vendorSuite.beforeTest( description, context, testCase );
            TestResult result = vendorSuite.executeTest( description, context, testCase );
            switch ( result.getResult() ) {
                case SUCCESS:
                    runNotifier.fireTestFinished( description );
                    break;
                case IGNORED:
                    runNotifier.fireTestIgnored( description );
                    break;
                case ERROR:
				runNotifier.fireTestFailure(new Failure(description, new RuntimeException(result.toStringWithLines())));
                    break;
            }
            resultFile.append( String.format( "%s,%s,%s,%s\n", folder, description.getClassName(), description.getMethodName(), result.getResult().toString() ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            vendorSuite.afterTest( description, context, testCase );
        }
    }

    @Override
    public Description getDescription() {
        return descr;
    }

}