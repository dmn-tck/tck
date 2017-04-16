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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * generates the index.html page
 */
public class IndexGenerator {
    private static final Logger logger = LoggerFactory.getLogger( IndexGenerator.class);

    public static void generatePage(Reporter.Parameters params, Configuration cfg, ReportHeader header, Map<String, Vendor> results) {
        logger.info( "Generating index.html" );
        try {
            Template temp = cfg.getTemplate( "/templates/index.ftl" );

            Map<String, Object> data = new HashMap<>(  );
            data.put( "vendors", results );
            data.put( "header", header );

            Writer out = new FileWriter( params.output.getAbsolutePath() + "/index.html" );
            temp.process( data, out );
            out.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( TemplateException e ) {
            e.printStackTrace();
        }
    }

}
