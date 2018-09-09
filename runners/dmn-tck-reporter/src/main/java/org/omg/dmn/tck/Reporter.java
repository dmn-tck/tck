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
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.cli.*;
import org.omg.dmn.tck.marshaller.TckMarshallingHelper;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.util.ReportHelper;
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
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

public class Reporter {

    private static final Logger logger = LoggerFactory.getLogger( Reporter.class );
    public static final String GLYPHICON_SUCCESS = "glyphicon-ok icon-green";
    public static final String GLYPHICON_FAILURE = "glyphicon-remove icon-red";
    public static final String GLYPHICON_WARNING = "glyphicon-alert icon-yellow";
    public static final String GLYPHICON_MISSING = "glyphicon-minus icon-black";

    public static void main(String[] args) {
        Parameters params = parseCommandLine( args );

        runReport( params );
    }

    private static void runReport(Parameters params) {
        Map<String, TestCasesData> tests = loadTestCases( params );
        Map<String, Vendor> results = loadTestResults( params, tests );
        Map<String, List<TestCasesData>> labels = sortTestsByLabel( tests );

        ReportHeader header = createReportHeader( tests, labels, results );
        Map<String, ReportTable> tableByLabels = createTableByLabels( labels, results );
        Map<String, ReportChart> chartByLabels = createChartByLabels( labels, results);
        Map<String, ReportChart> chartByLabelsPercent = createChartByLabelsPercent( labels, results);
        ReportTable tableAllTests = createTableAllTests( params, tests.values() );
        Map<String, ReportTable> tableAllTestsByVendor = createTableAllTestsByVendor( params, tests.values(), results );
        Map<String, List<ReportTable>> tableIndividualLabels = createTableByIndividualLabels( params, labels, results );

        logger.info( "Generating report" );
        Configuration cfg = createFreemarkerConfiguration();

        IndexGenerator.generatePage( params, cfg, header, results );
        for( Vendor vendor : results.values() ) {
            OverviewGenerator.generatePage( params, cfg, vendor, header, tableByLabels.get( vendor.getFileNameId() ),
                                            chartByLabels.get( vendor.getFileNameId() ), chartByLabelsPercent.get( vendor.getFileNameId() ) );
            DetailGenerator.generatePage( params, cfg, vendor, header, tableAllTestsByVendor.get( vendor.getFileNameId() ), tableIndividualLabels.get( vendor.getFileNameId() ) );
        }
        TestsGenerator.generatePage( params, cfg, header, tableAllTests );
        GlossaryGenerator.generatePage( params, cfg, header );
    }

    private static ReportHeader createReportHeader(Map<String, TestCasesData> tests, Map<String, List<TestCasesData>> labels, Map<String, Vendor> results) {
        ReportHeader header = new ReportHeader(  );
        int totalTests = 0;
        for( TestCasesData tcd : tests.values() ) {
            totalTests += tcd.model.getTestCase().size();
        }
        header.setTotalLabels( labels.size() );
        header.setTotalTests( totalTests );
        header.setTotalProducts( results.size() );
        return header;
    }

    private static Map<String, List<ReportTable>> createTableByIndividualLabels(Parameters params, Map<String, List<TestCasesData>> tests, Map<String, Vendor> results) {
        Map<String, List<ReportTable>> tables = new HashMap<>(  );

        for( Map.Entry<String, List<TestCasesData>> entry : tests.entrySet() ) {
            List<TestCasesData> testList = entry.getValue();
            Map<String, ReportTable> rt = createTableAllTestsByVendor( params, testList, results );
            for( Map.Entry<String, ReportTable> reportEntry : rt.entrySet() ) {
                List<ReportTable> tablesForVendor = tables.get( reportEntry.getKey() );
                if( tablesForVendor == null ) {
                    tablesForVendor = new ArrayList<>(  );
                    tables.put( reportEntry.getKey(), tablesForVendor );
                }
                reportEntry.getValue().setTitle( entry.getKey() );
                tablesForVendor.add( reportEntry.getValue() );
            }
        }
        return tables;
    }

    private static ReportTable createTableAllTests(Parameters params, Collection<TestCasesData> tests) {
        ReportTable table = new ReportTable(  );
        ReportHelper.addHeader( table, "Compliance", "" );
        ReportHelper.addHeader( table, "Test Case", "" );
        ReportHelper.addHeader( table, "Test Suite", "" );
        ReportHelper.addHeader( table, "Doc", "" );
        ReportHelper.addHeader( table, "Source", "" );
        ReportHelper.addHeader( table, "Test", "" );
        ReportHelper.addHeader( table, "Description", "" );
        String[] text = new String[3];
        TableRow[] parents = new TableRow[3];
        for( TestCasesData tcd : tests ) {
            for( TestCases.TestCase tc : tcd.model.getTestCase() ) {
                TableRow row = new TableRow(  );
                String[] split = tcd.folder.split( "/" );
                ReportHelper.addRowCell( row, split[0], "" );
                ReportHelper.addRowCell( row, split[1], "" );
                ReportHelper.addRowCell( row, tcd.testCaseName, "" );
                if( Files.exists( params.testsFolder.resolve( tcd.folder+"/"+split[1]+".pdf" ) ) ) {
                    ReportHelper.addRowCell(row, split[1] + ".pdf", "glyphicon glyphicon-book");
                } else if( Files.exists( params.testsFolder.resolve( tcd.folder+"/Readme.md" ) ) ) {
                    ReportHelper.addRowCell(row, "Readme.md", "glyphicon glyphicon-book");
                } else {
                    ReportHelper.addRowCell( row, "NA", "glyphicon glyphicon-book" );
                }
                ReportHelper.addRowCell( row, "", "glyphicon glyphicon-folder-open" );
                ReportHelper.addRowCell( row, tc.getId() != null ? tc.getId() : "[no ID]", "" );
                ReportHelper.addRowCell( row, tc.getDescription() != null ? tc.getDescription() : "", "" );
                table.getRows().add( row );
                updateRowspan( text, parents, tcd, row, split );
            }
        }
        return table;
    }

    private static Map<String, ReportTable> createTableAllTestsByVendor(Parameters params, Collection<TestCasesData> tests, Map<String, Vendor> results) {
        Map<String, ReportTable> tables = new HashMap<>(  );
        for( Vendor vendor : results.values() ) {
            ReportTable table = new ReportTable(  );
            ReportHelper.addHeader( table, "Compliance", "" );
            ReportHelper.addHeader( table, "Test Case", "" );
            ReportHelper.addHeader( table, "Test Suite", "" );
            ReportHelper.addHeader( table, "Doc", "" );
            ReportHelper.addHeader( table, "Source", "" );
            ReportHelper.addHeader( table, "Test", "" );
            ReportHelper.addHeader( table, vendor.getProduct(), vendor.getVersion() );
            ReportHelper.addHeader( table, "Comment", "" );
            String[] text = new String[3];
            TableRow[] parents = new TableRow[3];
            int succeeded = 0;
            int total = 0;
            for( TestCasesData tcd : tests ) {
                for( TestCases.TestCase tc : tcd.model.getTestCase() ) {
                    TableRow row = new TableRow(  );
                    String[] split = tcd.folder.split( "/" );
                    ReportHelper.addRowCell( row, split[0], "" );
                    ReportHelper.addRowCell( row, split[1], "" );
                    ReportHelper.addRowCell( row, tcd.testCaseName, "" );
                    if( Files.exists( params.testsFolder.resolve( tcd.folder+"/"+split[1]+".pdf" ) ) ) {
                        ReportHelper.addRowCell(row, split[1] + ".pdf", "glyphicon glyphicon-book");
                    } else if( Files.exists( params.testsFolder.resolve( tcd.folder+"/Readme.md" ) ) ) {
                        ReportHelper.addRowCell(row, "Readme.md", "glyphicon glyphicon-book");
                    } else {
                        ReportHelper.addRowCell( row, "NA", "glyphicon glyphicon-book" );
                    }
                    ReportHelper.addRowCell( row, "", "glyphicon glyphicon-folder-open" );
                    ReportHelper.addRowCell( row, tc.getId() != null ? tc.getId() : "[no ID]", "" );
                    TestResult r = vendor.getResults().get( createTestKey( tcd.folder, tcd.testCaseName, tc.getId() ) );
                    String icon = null;
                    String tooltip = null;
                    if( r == null ) {
                        icon = GLYPHICON_MISSING;
                        tooltip = "Missing";
                    } else if( r.getResult() == TestResult.Result.SUCCESS ) {
                        icon = GLYPHICON_SUCCESS;
                        tooltip = "Succeeded";
                        succeeded++;
                    } else if( r.getResult() == TestResult.Result.ERROR ) {
                        icon = GLYPHICON_FAILURE;
                        tooltip = "Failed";
                    } else {
                        icon = GLYPHICON_WARNING;
                        tooltip = "Not Supported";
                    }
                    String comment = r != null ? r.getComment() : "";
                    ReportHelper.addRowCell( row, "", icon );
                    ReportHelper.addRowCell( row, comment, "" );
                    ReportHelper.addRowCell( row, tooltip, "" );
                    total++;
                    table.getRows().add( row );

                    updateRowspan( text, parents, tcd, row, split );
                }
            }
            table.getTotals().add( succeeded+"/"+total );
            tables.put( vendor.getFileNameId(), table );
        }
        return tables;
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

    private static Map<String, ReportChart> createChartByLabels(Map<String, List<TestCasesData>> labels, Map<String, Vendor> results) {
        Map<String, ReportChart> charts = new HashMap<>(  );
        for( Vendor v : results.values() ) {
            ReportChart chart = new ReportChart();
            chart.setName( "cbl"+charts.size() );
            chart.setTitle( v.getName() + " " + v.getVersion() );
            charts.put( v.getFileNameId(), chart );
        }

        for( Map.Entry<String, List<TestCasesData>> lbl : labels.entrySet() ) {
            int[] success = new int[results.size()];
            int[] ignored = new int[results.size()];
            int[] failed = new int[results.size()];
            int[] total = new int[results.size()];
            calculateTotals( results, lbl, success, ignored, failed, total );
            int i = 0;
            for( Vendor vendor : results.values() ) {
                ReportChart.DataPoint row = new ReportChart.DataPoint(  );
                row.setLabel( lbl.getKey() );
                row.getData().add( success[i] );
                row.getData().add( ignored[i] );
                row.getData().add( failed[i] );
                row.getData().add( total[i] - success[i] - ignored[i] - failed[i] );
                charts.get( vendor.getFileNameId() ).getDataset().add( row );
                charts.get( vendor.getFileNameId() ).getLabels().add( lbl.getKey() );
                i++;
            }
        }
        return charts;
    }

    private static Map<String, ReportChart> createChartByLabelsPercent(Map<String, List<TestCasesData>> labels, Map<String, Vendor> results) {
        Map<String, ReportChart> charts = new HashMap<>(  );
        for( Vendor v : results.values() ) {
            ReportChart chart = new ReportChart();
            chart.setName( "cblp"+charts.size() );
            chart.setTitle( v.getName() + " " + v.getVersion() );
            charts.put( v.getFileNameId(), chart );
        }

        for( Map.Entry<String, List<TestCasesData>> lbl : labels.entrySet() ) {
            int[] success = new int[results.size()];
            int[] ignored = new int[results.size()];
            int[] failed = new int[results.size()];
            int[] total = new int[results.size()];
            calculateTotals( results, lbl, success, ignored, failed, total );
            int i = 0;
            for( Vendor vendor : results.values() ) {
                ReportChart.DataPoint row = new ReportChart.DataPoint(  );
                row.setLabel( lbl.getKey() );
                int sp = (int) (((double) success[i] / (double) total[i]) * 100);
                int ip = (int) (((double) ignored[i] / (double) total[i]) * 100);
                int fp = (int) (((double) failed[i] / (double) total[i]) * 100);
                int totalReported = success[i] + ignored[i] + failed[i];
                int mp = totalReported < total[i] ? 100 - sp - ip - fp : 0;
                if( sp + ip + fp + mp < 100 ) {
                    // hack to eliminate rounding errors on the chart data
                    if( sp > 0 ) {
                        sp += 100-sp-ip-fp;
                    } else if( ip > 0 ) {
                        ip += 100-sp-ip-fp;
                    }
                }
                row.getData().add( sp );
                row.getData().add( ip );
                row.getData().add( fp );
                row.getData().add( mp );
                charts.get( vendor.getFileNameId() ).getDataset().add( row );
                charts.get( vendor.getFileNameId() ).getLabels().add( lbl.getKey() );
                i++;
            }
        }
        return charts;
    }

    private static void calculateTotals(Map<String, Vendor> results, Map.Entry<String, List<TestCasesData>> lbl, int[] success, int[] ignored, int[] failed, int[] total) {
        for( TestCasesData tcd : lbl.getValue() ) {
            for( TestCases.TestCase tc : tcd.model.getTestCase() ) {
                int index = 0;
                for( Vendor v : results.values() ) {
                    String key = createTestKey( tcd.folder, tcd.testCaseName, tc.getId() );
                    TestResult r = v.getResults().get( key );
                    if( r != null && r.getResult() == TestResult.Result.SUCCESS ) {
                        success[index]++;
                    } else if( r != null && r.getResult() == TestResult.Result.ERROR ) {
                        failed[index]++;
                    } else if( r != null && r.getResult() == TestResult.Result.IGNORED ) {
                        ignored[index]++;
                    }
                    total[index]++;
                    index++;
                }
            }
        }
    }

    private static Map<String, ReportTable> createTableByLabels(Map<String, List<TestCasesData>> labels, Map<String, Vendor> results) {
        Map<String, ReportTable> rt = new HashMap<>(  );

        for( Vendor v : results.values() ) {
            ReportTable table = new ReportTable(  );
            ReportHelper.addHeader( table, "Label", "" );
            ReportHelper.addHeader( table, v.getName(), v.getVersion() );
            rt.put( v.getFileNameId(), table );
        }
        for( Map.Entry<String, List<TestCasesData>> lbl : labels.entrySet() ) {
            for( Vendor v : results.values() ) {
                ReportTable table = rt.get( v.getFileNameId() );
                TableRow row = new TableRow(  );
                ReportHelper.addRowCell( row, lbl.getKey(), "" );
                int success = 0;
                int total = 0;
                for( TestCasesData tcd : lbl.getValue() ) {
                    for( TestCases.TestCase tc : tcd.model.getTestCase() ) {
                        String key = createTestKey( tcd.folder, tcd.testCaseName, tc.getId() );
                        TestResult r = v.getResults().get( key );
                        if( r != null && r.getResult() == TestResult.Result.SUCCESS ) {
                            success++;
                        }
                        total++;
                    }
                }
                String icon = null;
                if( success == total ) {
                    icon = GLYPHICON_SUCCESS;
                } else if( success == 0 ) {
                    icon = GLYPHICON_FAILURE;
                } else {
                    icon = GLYPHICON_WARNING;
                }
                ReportHelper.addRowCell( row, success + "/" + total, icon );
                table.getRows().add( row );
            }
        }
        return rt;
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
                    tests.put(createTestCaseKey(folder, tcdname), tcdd );
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

    private static Map<String, Vendor> loadTestResults(Parameters params, Map<String, TestCasesData> tests) {
        logger.info( "Loading test results from folder "+params.input.getName() );
        Map< String, Vendor > results = new TreeMap<>(  );

        File[] vendors = params.input.listFiles( (dir, name) -> !name.startsWith( "." ) && !name.endsWith( ".html" ) );
        for( File vendor : vendors ) {
            File[] versions = vendor.listFiles( (dir, name) -> !name.startsWith( "." ) && !name.endsWith( ".html" ) );
            for( File version : versions ) {
                File[] propertiesFile = version.listFiles( (dir, name) -> name.equals( "tck_results.properties" ) );
                Properties properties = new Properties();
                if( propertiesFile.length == 1 ) {
                    try {
                        properties.load( new FileReader( propertiesFile[0] ) );
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }
                File[] resultsFile = version.listFiles( (dir, name) -> name.equals( "tck_results.csv" ) );

                if( resultsFile.length == 1 ) {
                    Map< String, TestResult > testResults = new TreeMap<>(  );
                    try (Stream<String> lines = Files.lines( resultsFile[0].toPath() ) ) {
                        // skip the file header and load the rest
                        lines.forEach( l -> {
                            String[] fields = l.split( ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                            String comment = fields.length > 4 ? fields[4] : "";
                            String testFolder = ReportHelper.removeQuotes( fields[0] );
                            String testSuit = ReportHelper.removeQuotes( fields[1] );
                            String testId = ReportHelper.removeQuotes( fields[2] );
                            String testKey = createTestKey( testFolder, testSuit, testId );
                            TestCasesData tcd = tests.get(createTestCaseKey(testFolder, testSuit));
                            if( tcd != null && tcd.model.getTestCase().stream().anyMatch( tc -> tc.getId() != null && tc.getId().equals( testId ) ) ) {
                                // only record results for which the test exist (i.e., was not removed from the test set)
                                TestResult testResult = new TestResult(
                                        testFolder,
                                        testSuit,
                                        testId,
                                        TestResult.Result.fromString( ReportHelper.removeQuotes( fields[3] ) ),
                                        ReportHelper.removeQuotes( comment ) );
                                testResults.put( testKey, testResult );
                            }
                        });
                    } catch (IOException e) {
                        logger.error( "Error reading input file '"+params.input.getName()+"'", e );
                        continue;
                    }
                    LocalDate lastUpdate;
                    try {
                        String luStr = properties.getProperty( "last.update" );
                        if( luStr != null ) {
                            lastUpdate = LocalDate.parse( luStr );
                        } else {
                            lastUpdate = LocalDate.now();
                        }
                    } catch ( Exception e ) {
                        lastUpdate = LocalDate.now();
                    }
                    Vendor v = new Vendor( properties.getProperty( "vendor.name" ).trim(),
                                           properties.getProperty( "vendor.url" ).trim(),
                                           properties.getProperty( "product.name" ).trim(),
                                           properties.getProperty( "product.url" ).trim(),
                                           properties.getProperty( "product.version" ).trim(),
                                           properties.getProperty( "product.comment" ).trim(),
                                           testResults,
                                           lastUpdate,
                                           Optional.ofNullable(properties.getProperty("instructions.url")).map(String::trim).orElse(null));
                    results.put( v.getName()+" / "+v.getVersion(), v );
                    logger.info( testResults.size() + " test results loaded for vendor "+v );
                }
            }
        }
        return results;
    }

    private static String createTestCaseKey(String testFolder, String testSuit) {
        return testFolder+"/"+testSuit;
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
                    params.output = new File( outputFolder );
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

    public static class Parameters {
        public File input;
        public Path testsFolder;
        public File output;
    }

}
