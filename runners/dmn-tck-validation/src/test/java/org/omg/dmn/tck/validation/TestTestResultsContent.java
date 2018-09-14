
package org.omg.dmn.tck.validation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestTestResultsContent {

    private static final Logger LOG = LoggerFactory.getLogger(TestTestResultsContent.class);
    private final File vendorResultParentDir;
    private final String vendorID;

    public TestTestResultsContent(File vendorResultParentDir, String vendorID) {
        this.vendorResultParentDir = vendorResultParentDir;
        this.vendorID = vendorID;
    }

    @Parameters(name = "Vendor {1} results")
    public static Iterable<Object[]> data() {
        List<Object[]> vendorResults = new ArrayList<>();
        File parentDir = new File("../../TestResults");
        for (File vendorResultParentDir : parentDir.listFiles(File::isDirectory)) {
            vendorResults.add(new Object[]{vendorResultParentDir, vendorResultParentDir.getName()});
        }
        return vendorResults;
    }

    @Test
    public void checkVendorResultsDir() {
        LOG.info("Checking Vendor results directory: {}", vendorResultParentDir);
        
        File[] versionDirs = vendorResultParentDir.listFiles(File::isDirectory);
        assertEquals("Only 1 version per Vendor is allowed, please consider removing old versions.", 1, versionDirs.length);

        File versionDir = versionDirs[0];
        File[] files = versionDir.listFiles(File::isFile);

        LOG.info("Checking {} version {} directory...", vendorID, versionDir.getName());

        Optional<File> csvFile = Stream.of(files).filter(f -> f.getName().equals("tck_results.csv")).findFirst();
        assertTrue("Cannot find tck_results.csv file inside: " + versionDir, csvFile.isPresent());

        Optional<File> propFile = Stream.of(files).filter(f -> f.getName().equals("tck_results.properties")).findFirst();
        assertTrue("Cannot find tck_results.properties file inside: " + versionDir, propFile.isPresent());
        try {
            new Properties().load(new FileReader(propFile.get()));
        } catch (IOException e) {
            LOG.error("Unable to read tck_results.properties file.", e);
            fail("Unable to read tck_results.properties file.");
        }
    }
}
