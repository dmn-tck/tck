/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
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

package org.omg.dmn.tck;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.omg.dmn.tck.util.ReportHelper.addHeader;
import static org.omg.dmn.tck.util.ReportHelper.addRowCell;
import static org.omg.dmn.tck.util.ReportHelper.removeQuotes;

/**
 * generates the glossary.html page
 */
public class GlossaryGenerator {
    private static final Logger logger = LoggerFactory.getLogger( GlossaryGenerator.class);

    public static void generatePage(Reporter.Parameters params, Configuration cfg, ReportHeader header ) {
        logger.info( "Generating glossary.html" );
        try {
            Template temp = cfg.getTemplate( "/templates/glossary.ftl" );
            List<TestLabel> testLabels = loadLabels( params );
            ReportTable tableAllLabels = createTableAllLabels( params, testLabels );

            Map<String, Object> data = new HashMap<>(  );
            data.put( "header", header );
            data.put( "tAllLabels", tableAllLabels );

            Writer out = new FileWriter( params.output.getAbsolutePath() + "/glossary.html" );
            temp.process( data, out );
            out.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( TemplateException e ) {
            e.printStackTrace();
        }
    }

    private static ReportTable createTableAllLabels(Reporter.Parameters params, List<TestLabel> testLabels) {
        ReportTable table = new ReportTable(  );
        addHeader( table, "Label", "" );
        addHeader( table, "Description", "" );
        addHeader( table, "XPath", "" );
        for( TestLabel label : testLabels ) {
            TableRow row = new TableRow(  );
            addRowCell( row, label.getLabel(), "" );
            addRowCell( row, label.getDescription(), "" );
            addRowCell( row, label.getXpath(), "" );
            table.getRows().add( row );
        }
        return table;
    }

    private static List<TestLabel> loadLabels(Reporter.Parameters params) {
        List<TestLabel> labels = new ArrayList<>(  );
        String inputFile = "/templates/test-labels.csv";
        try {
            Path source = Paths.get( GlossaryGenerator.class.getResource( inputFile ).toURI() );
            try (Stream<String> lines = Files.lines( source ) ) {
                lines.forEach( l -> {
                    if( ! l.trim().isEmpty() ) {
                        String[] fields = l.split( ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                        if( fields.length != 3 ) {
                            logger.error( "Incorrect number of fields reading line: " + l );
                        } else {
                            String label = removeQuotes( fields[0] );
                            String descr = removeQuotes( fields[1] );
                            String xpath = removeQuotes( fields[2] );
                            TestLabel testLabel = new TestLabel(
                                    label,
                                    descr,
                                    xpath );
                            labels.add( testLabel );
                        }
                    }
                });
            } catch (IOException e) {
                logger.error( "Error reading input file '"+inputFile+"'", e );
            }
        } catch ( Exception e ) {
            logger.error( "Input file not found: '"+inputFile+"'", e );
        }
        return labels;
    }

}
