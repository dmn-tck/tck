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

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * generates the about.html page
 */
public class AboutGenerator {
    private static final Logger logger = LoggerFactory.getLogger( AboutGenerator.class);

    public static void generatePage(Reporter.Parameters params, Configuration cfg, ReportHeader header ) {
        logger.info( "Generating about.html" );
        try {
            Template temp = cfg.getTemplate( "/templates/about.ftl" );

            Map<String, Object> data = new HashMap<>(  );

            Writer out = new FileWriter( params.output.getAbsolutePath() + "/about.html" );
            temp.process( data, out );
            out.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( TemplateException e ) {
            e.printStackTrace();
        }
    }

}
