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
import java.util.List;
import java.util.Map;

/**
 * generates the detail.html page
 */
public class DetailGenerator {
    private static final Logger logger = LoggerFactory.getLogger( DetailGenerator.class);

    public static void generatePage(Reporter.Parameters params, Configuration cfg, Vendor vendor, ReportHeader header,
                                    ReportTable tableAllTests, List<ReportTable> tableIndividualLabels ) {
        logger.info( "Generating detail_{}.html", vendor.getFileNameId() );
        try {
            Template temp = cfg.getTemplate( "/templates/detail.ftl" );
            Map<String, Object> data = new HashMap<>(  );
            data.put( "vendor", vendor );
            data.put( "header", header );
            data.put( "tAllTests", tableAllTests );
            data.put( "tIndLabels", tableIndividualLabels );

            Writer out = new FileWriter( params.output.getAbsolutePath() + "/detail_"+vendor.getFileNameId()+".html" );
            temp.process( data, out );
            out.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( TemplateException e ) {
            e.printStackTrace();
        }
    }

}
