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
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.cli.*;
import org.omg.dmn.tck.marshaller.TckMarshallingHelper;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Reporter {

    private static final Logger logger = LoggerFactory.getLogger( Reporter.class );
    public static final String GLYPHICON_SUCCESS = "glyphicon-ok icon-green";
    public static final String GLYPHICON_FAILURE = "glyphicon-remove icon-red";
    public static final String GLYPHICON_WARNING = "glyphicon-alert icon-yellow";

    public static void main(String[] args) {
        Parameters params = parseCommandLine( args );

        runReport( params );
    }

    private static void runReport(Parameters params) {
        Map<String, Vendor> results = loadTestResults( params );
        Map<String, TestCasesData> tests = loadTestCases( params );
        Map<String, List<TestCasesData>> labels = sortTestsByLabel( tests );

        ReportTable tableByLabels = createTableByLabels( labels, results );
        ReportTable tableAllTests = createTableAllTests( tests.values(), results );
        List<ReportTable> tableIndividualLabels = createTableByIndividualLabels( labels, results );

        logger.info( "Generating report" );
        try {
            Configuration cfg = createFreemarkerConfiguration();
            Template temp = cfg.getTemplate( "/templates/report.ftl" );

            Map<String, Object> data = new HashMap<>(  );
            data.put( "tByLabels", tableByLabels );
            data.put( "tAllTests", tableAllTests );
            data.put( "tIndLabels", tableIndividualLabels );

            Writer out = new FileWriter( params.output );
            temp.process( data, out );
            out.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( TemplateException e ) {
            e.printStackTrace();
        }

        labels.entrySet().forEach( e -> {
            System.out.println(e.getKey() + " = " + e.getValue().size() );
        } );
    }

    private static List<ReportTable> createTableByIndividualLabels(Map<String, List<TestCasesData>> tests, Map<String, Vendor> results) {
        List<ReportTable> tables = new ArrayList<>(  );

        for( Map.Entry<String, List<TestCasesData>> entry : tests.entrySet() ) {
            List<TestCasesData> testList = entry.getValue();
            ReportTable rt = createTableAllTests( testList, results );
            rt.setTitle( entry.getKey() );
            tables.add( rt );
        }
        return tables;
    }

    private static ReportTable createTableAllTests(Collection<TestCasesData> tests, Map<String, Vendor> results) {
        ReportTable table = new ReportTable(  );
        addHeader( table, "Compliance", "" );
        addHeader( table, "Test Case", "" );
        addHeader( table, "Test Suite", "" );
        addHeader( table, "Test", "" );
        for( Vendor v : results.values() ) {
            addHeader( table, v.getName(), v.getVersion() );
        }
        String[] text = new String[3];
        TableRow[] parents = new TableRow[3];
        for( TestCasesData tcd : tests ) {
            for( TestCases.TestCase tc : tcd.model.getTestCase() ) {
                TableRow row = new TableRow(  );
                String[] split = tcd.folder.split( "/" );
                addRowCell( row, split[0], "" );
                addRowCell( row, split[1], "" );
                addRowCell( row, tcd.testCaseName, "" );
                addRowCell( row, tc.getId() != null ? tc.getId() : "[no ID]", "" );
                for( Vendor v : results.values() ) {
                    TestResult r = v.getResults().get( createTestKey( tcd.folder, tcd.testCaseName, tc.getId() ) );
                    String icon = null;
                    if( r == null ) {
                        icon = GLYPHICON_WARNING;
                    } else if( r.getResult() == TestResult.Result.SUCCESS ) {
                        icon = GLYPHICON_SUCCESS;
                    } else if( r.getResult() == TestResult.Result.ERROR ) {
                        icon = GLYPHICON_FAILURE;
                    } else {
                        icon = GLYPHICON_WARNING;
                    }
                    addRowCell( row, "", icon );
                }
                table.getRows().add( row );

                updateRowspan( text, parents, tcd, row, split );
            }
        }
        return table;
    }

    private static void updateRowspan(String[] text, TableRow[] parents, TestCasesData tcd, TableRow row, String[] split) {
        if( text[0] == null || !text[0].equals( split[0] ) ) {
            text[0] = split[0];
            text[1] = null;
            text[2] = null;
            parents[0] = row;
        } else {
            parents[0].getRowspan().set( 0, parents[0].getRowspan().get( 0 ) + 1 );
            row.getRowspan().set( 0, 0 );
        }
        if( text[1] == null || !text[1].equals( split[1] ) ) {
            text[1] = split[1];
            text[2] = null;
            parents[1] = row;
        } else {
            parents[1].getRowspan().set( 1, parents[1].getRowspan().get( 1 ) + 1 );
            row.getRowspan().set( 1, 0 );
        }
        if( text[2] == null || !text[2].equals( tcd.testCaseName ) ) {
            text[2] = tcd.testCaseName;
            parents[2] = row;
        } else {
            parents[2].getRowspan().set( 2, parents[2].getRowspan().get( 2 ) + 1 );
            row.getRowspan().set( 2, 0 );
        }
    }

    private static void addRowCell(TableRow row, String text, String icon) {
        row.getText().add( text );
        row.getIcons().add( icon );
        row.getRowspan().add( 1 );
    }

    private static void addHeader(ReportTable table, String t, String d) {
        table.getHeaders().add( t );
        table.getHeaderDetails().add( d );
    }

    private static ReportTable createTableByLabels(Map<String, List<TestCasesData>> labels, Map<String, Vendor> results) {
        ReportTable table = new ReportTable(  );
        addHeader( table, "", "" );
        for( Vendor v : results.values() ) {
            addHeader( table, v.getName(), v.getVersion() );
        }
        for( Map.Entry<String, List<TestCasesData>> lbl : labels.entrySet() ) {
            TableRow row = new TableRow(  );
            addRowCell( row, lbl.getKey(), "" );
            int[] success = new int[results.size()];
            int[] total = new int[results.size()];;
            for( TestCasesData tcd : lbl.getValue() ) {
                for( TestCases.TestCase tc : tcd.model.getTestCase() ) {
                    int index = 0;
                    for( Vendor v : results.values() ) {
                        String key = createTestKey( tcd.folder, tcd.testCaseName, tc.getId() );
                        TestResult r = v.getResults().get( key );
                        if( r != null && r.getResult() == TestResult.Result.SUCCESS ) {
                            success[index]++;
                        }
                        total[index]++;
                        index++;
                    }
                }
            }
            for( int i = 0; i < results.size(); i++ ) {
                String icon = null;
                if( success[i] == total[i] ) {
                    icon = GLYPHICON_SUCCESS;
                } else if( success[i] == 0 ) {
                    icon = GLYPHICON_FAILURE;
                } else {
                    icon = GLYPHICON_WARNING;
                }
                addRowCell( row, success[i]+"/"+total[i], icon );
            }
            table.getRows().add( row );
        }
        return table;
    }

    private static Configuration createFreemarkerConfiguration() {
        Configuration cfg = new Configuration();
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler( TemplateExceptionHandler.RETHROW_HANDLER );
        cfg.setClassForTemplateLoading( Reporter.class, "/" );
        return cfg;
    }

    private static Map<String, List<TestCasesData>> sortTestsByLabel(Map<String, TestCasesData> tests) {
        logger.info( "Sorting test cases by labels" );
        Map<String, List<TestCasesData>> labels = new TreeMap<>(  );
        tests.values().forEach( tcd -> {
            if( tcd.model.getLabels() != null && tcd.model.getLabels().getLabel() != null && ! tcd.model.getLabels().getLabel().isEmpty() ) {
                tcd.model.getLabels().getLabel().forEach( lbl -> {
                    addTestCaseToLabelsList( labels, tcd, lbl );
                } );
            } else {
                addTestCaseToLabelsList( labels, tcd, "[no label]" );
            }
        } );
        return labels;
    }

    private static void addTestCaseToLabelsList(Map<String, List<TestCasesData>> labels, TestCasesData tcd, String lbl) {
        List<TestCasesData> tcds = labels.get( lbl );
        if( tcds == null ) {
            tcds = new ArrayList<>(  );
            labels.put( lbl, tcds );
        }
        tcds.add( tcd );
    }

    private static Map<String, TestCasesData> loadTestCases(Parameters params) {
        logger.info( "Loading test case definitions" );
        Map<String, TestCasesData> tests = new TreeMap<>(  );
        List<URL> testCaseURLs = getTestCaseURLs(params);
        int testCount = 0;
        for ( URL url : testCaseURLs ) {
            File tcFolder = null;
            try {
                tcFolder = new File( url.toURI() );
            } catch ( URISyntaxException e ) {
                logger.error( "Error loading test case "+url, e );
                System.exit( 1 );
            }
            final String tcName = tcFolder.getName();

            File[] tcfiles = tcFolder.listFiles( new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.matches( tcName + "-test-\\d\\d.xml" );
                }
            } );
            for ( File tcfile : tcfiles ) {
                try {
                    TestCases tcd = TckMarshallingHelper.load( new FileInputStream( tcfile ) );
                    testCount += tcd.getTestCase().size();
                    String parent = tcfile.getParent();
                    String folder = parent != null ? parent.substring( parent.lastIndexOf( '/', parent.lastIndexOf( '/' ) - 1 ) + 1 ) : "<unknown>";
                    String tcdname = tcfile.getName();
                    tcdname = tcdname.substring( 0, tcdname.lastIndexOf( '.' ) ).replaceAll( "\\.", "/" );
                    TestCasesData tcdd = new TestCasesData( folder, tcdname, tcd );
                    tests.put( folder+"/"+tcdname, tcdd );
                } catch ( FileNotFoundException e ) {
                    logger.error( "Error loading test case "+tcfile, e );
                    System.exit( 1 );
                } catch ( JAXBException e ) {
                    logger.error( "Error loading test case "+tcfile, e );
                    System.exit( 1 );
                }
            }
        }
        logger.info( tests.size() + " test suites loaded, with "+testCount+" unit tests" );
        return tests;
    }

    public static List<URL> getTestCaseURLs( Parameters params ) {
        List<URL> testCases = new ArrayList<>(  );
        FilenameFilter filenameFilter = (dir, name) -> name.matches( "\\d\\d\\d\\d-.*" );
        getTestURLs( testCases, params.testsFolder.resolve( "compliance-level-2" ).toFile(), filenameFilter );
        getTestURLs( testCases, params.testsFolder.resolve( "compliance-level-3" ).toFile(), filenameFilter );
        return testCases;
    }

    private static void getTestURLs(List<URL> testCases, File parentFolder, FilenameFilter filenameFilter) {
        for( File file : parentFolder.listFiles( filenameFilter ) ) {
            try {
                testCases.add( file.toURI().toURL() );
            } catch ( MalformedURLException e ) {
                logger.error( "Error retrieving list of test cases", e );
                System.exit( 1 );
            }
        }
    }

    private static Map<String, Vendor> loadTestResults(Parameters params) {
        logger.info( "Loading test results from folder "+params.input.getName() );
        Map< String, Vendor > results = new TreeMap<>(  );

        File[] vendors = params.input.listFiles( (dir, name) -> !name.startsWith( "." ) );
        for( File vendor : vendors ) {
            File[] versions = vendor.listFiles( (dir, name) -> !name.startsWith( "." ) );
            for( File version : versions ) {
                File[] resultsFile = version.listFiles( (dir, name) -> name.equals( "tck_results.csv" ) );
                if( resultsFile.length == 1 ) {
                    Map< String, TestResult > testResults = new TreeMap<>(  );
                    try (Stream<String> lines = Files.lines( resultsFile[0].toPath() ) ) {
                        // skip the file header and load the rest
                        lines.forEach( l -> {
                            String[] fields = l.split( "," );
                            TestResult testResult = new TestResult( fields[0], fields[1], fields[2], TestResult.Result.fromString( fields[3] ) );
                            testResults.put( createTestKey( fields[0], fields[1], fields[2] ), testResult );
                        });
                    } catch (IOException e) {
                        logger.error( "Error reading input file '"+params.input.getName()+"'", e );
                        continue;
                    }
                    Vendor v = new Vendor( vendor.getName(),
                                           version.getName(),
                                           testResults );
                    results.put( v.getName()+" / "+v.getVersion(), v );
                    logger.info( testResults.size() + " test results loaded." );
                }
            }
        }
        return results;
    }

    private static String createTestKey(String folder, String testSuite, String test) {
        return folder+"/"+testSuite+"/"+test;
    }

    private static Parameters parseCommandLine(String[] args) {
        Options options = new Options();
        options.addOption( "h", "help", false, "prints this help" );
        options.addOption( "i", "input", true, "the root input folder where test results CSV files are" );
        options.addOption( "t", "tests", true, "the test cases folder" );
        options.addOption( "o", "output", true, "folder where to generate the report file" );

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine line = parser.parse( options, args );

            if( line.hasOption( "h" ) ) {
                printHelpAndExit( options );
            }
            Parameters params = new Parameters();

            if( line.hasOption( "i" ) ) {
                String fileName = line.getOptionValue( "i" );
                try {
                    params.input = new File( fileName );
                    if( ! params.input.exists() || !params.input.canRead() ) {
                        logger.error( "Error accessing input folder '"+fileName+"'" );
                        printHelpAndExit( options );
                    }
                } catch ( Exception e ) {
                    logger.error( "Error accessing input folder '"+fileName+"'", e );
                    printHelpAndExit( options );
                }
            } else {
                printHelpAndExit( options );
            }

            if( line.hasOption( "t" ) ) {
                String folderName = line.getOptionValue( "t" );
                try {
                    params.testsFolder = Paths.get( folderName );
                    File file = params.testsFolder.toFile();
                    if( ! file.exists() ) {
                        logger.error( "Error accessing tests folder '"+folderName+"'" );
                        printHelpAndExit( options );
                    }
                } catch ( Exception e ) {
                    logger.error( "Error accessing testsFolder '"+folderName+"'", e );
                    printHelpAndExit( options );
                }
            } else {
                printHelpAndExit( options );
            }

            if( line.hasOption( "o" ) ) {
                String outputFolder = line.getOptionValue( "o" );
                try {
                    params.output = new File( outputFolder+"/DMN_TCK_result.html" );
                    if( params.output.exists() ) {
                        params.output.delete();
                    }
                } catch ( Exception e ) {
                    logger.error( "Error accessing output folder '"+outputFolder+"'", e );
                    printHelpAndExit( options );
                }
            } else {
                printHelpAndExit( options );
            }

            return params;
        } catch ( ParseException e ) {
            logger.error( "Error parsing command line parameters", e );
            printHelpAndExit( options );
        }
        return null;
    }

    private static void printHelpAndExit(Options options) {
        new HelpFormatter().printHelp( "java -jar tck-reporter.jar", options );
        System.exit( 0 );
    }

    private static class Parameters {
        public File input;
        public Path testsFolder;
        public File output;
    }

    public static class Vendor {
        private String name;
        private String version;
        private Map<String, TestResult> results;

        public Vendor(String name, String version, Map<String, TestResult> results) {
            this.name = name;
            this.version = version;
            this.results = results;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Map<String, TestResult> getResults() {
            return results;
        }

        public void setResults(Map<String, TestResult> results) {
            this.results = results;
        }

        @Override
        public boolean equals(Object o) {
            if ( this == o ) return true;
            if ( !(o instanceof Vendor) ) return false;

            Vendor vendor = (Vendor) o;

            if ( name != null ? !name.equals( vendor.name ) : vendor.name != null ) return false;
            return version != null ? version.equals( vendor.version ) : vendor.version == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (version != null ? version.hashCode() : 0);
            return result;
        }
    }

    public static class TestCasesData {
        public String folder;
        public String testCaseName;
        public TestCases model;

        public TestCasesData(String folder, String testCaseName, TestCases model) {
            this.folder = folder;
            this.testCaseName = testCaseName;
            this.model = model;
        }
    }

    public static class ReportTable {
        private String title;
        private List<String> headers;
        private List<String> headerDetails;
        private List<TableRow> rows;

        public ReportTable() {
            this( "", new ArrayList<>(  ), new ArrayList<>(  ), new ArrayList<>(  ) );
        }

        public ReportTable(String title, List<String> headers, List<String> headerDetails, List<TableRow> rows) {
            this.title = title;
            this.headers = headers;
            this.headerDetails = headerDetails;
            this.rows = rows;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getHeaders() {
            return headers;
        }

        public List<String> getHeaderDetails() {
            return headerDetails;
        }

        public List<TableRow> getRows() {
            return rows;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setHeaders(List<String> headers) {
            this.headers = headers;
        }

        public void setHeaderDetails(List<String> headerDetails) {
            this.headerDetails = headerDetails;
        }

        public void setRows(List<TableRow> rows) {
            this.rows = rows;
        }
    }

    public static class TableRow {
        private List<String> icons;
        private List<String> text;
        private List<Integer> rowspan;

        public TableRow() {
            this(new ArrayList<>(  ), new ArrayList<>(  ), new ArrayList<>(  ) );
        }

        public TableRow(List<String> icons, List<String> text, List<Integer> rowspan) {
            this.icons = icons;
            this.text = text;
            this.rowspan = rowspan;
        }

        public List<String> getIcons() {
            return icons;
        }

        public void setIcons(List<String> icons) {
            this.icons = icons;
        }

        public List<String> getText() {
            return text;
        }

        public void setText(List<String> text) {
            this.text = text;
        }

        public List<Integer> getRowspan() {
            return rowspan;
        }

        public void setRowspan(List<Integer> rowspan) {
            this.rowspan = rowspan;
        }
    }

}
