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
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DmnTckSuite
        extends Suite {

    private static final Logger logger = LoggerFactory.getLogger( DmnTckSuite.class );

    private final Description           descr;
    private final DmnTckVendorTestSuite ntsuite;
    private final List<Runner>          runners;


    public DmnTckSuite(Class<?> clazz)
            throws InitializationError {
        super( clazz, Collections.<Runner>emptyList() );

        runners = new ArrayList<Runner>();

        try {
            ntsuite = (DmnTckVendorTestSuite) clazz.newInstance();
        } catch ( Exception e ) {
            logger.error( "Error instantiating test suite.", e );
            throw new InitializationError( e );
        }
        List<URI> uris = ntsuite.getTestCases();
        this.descr = Description.createSuiteDescription( "DMN TCK test suite" );

        for ( URI uri : uris ) {
            File tcFolder = new File( uri );
            final String tcName = tcFolder.getName();

            final File model = new File( tcFolder + "/" + tcName + ".dmn" );
            if ( !model.exists() ) {
                continue;
            }

            File[] tcfiles = tcFolder.listFiles( new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.matches( tcName + "-test-\\d\\d.xml" );
                }
            } );
            Arrays.sort( tcfiles );
            for ( File tcfile : tcfiles ) {
                DmnTckRunner runner = new DmnTckRunner( ntsuite, tcfile );
                runners.add( runner );
            }
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

}
