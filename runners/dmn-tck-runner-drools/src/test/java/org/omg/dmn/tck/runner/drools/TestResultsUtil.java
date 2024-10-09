package org.omg.dmn.tck.runner.drools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public final class TestResultsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestResultsUtil.class);

    private static final String TEST_RESULTS_FOLDER_PATH = ".." + File.separator + ".." + File.separator + "TestResults" + File.separator + "Drools";

    private static final String PRODUCT_NAME_KEY = "product.name";
    private static final String PRODUCT_VERSION_KEY = "product.version";
    private static final String PRODUCT_COMMENT_KEY = "product.comment";
    private static final String PRODUCT_URL_KEY = "product.url";
    private static final String VENDOR_NAME_KEY = "vendor.name";
    private static final String VENDOR_URL_KEY = "vendor.url";
    private static final String LAST_UPDATE_KEY = "last.update";
    private static final String INSTRUCTIONS_URL_KEY = "instructions.url";

    private static final String PRODUCT_NAME_VALUE = "Apache KIE Drools";
    private static final String PRODUCT_URL_VALUE = "https://www.kie.apache.org";
    private static final String PRODUCT_COMMENT_VALUE = "Apache KIE (includes Drools) provides full compliance level 3 authoring and runtime execution.";
    private static final String VENDOR_NAME_VALUE = "The Apache Software Foundation";
    private static final String VENDOR_URL_VALUE = "https://apache.org/";
    private static final String INSTRUCTIONS_URL_VALUE = "https://github.com/dmn-tck/tck/tree/master/runners/dmn-tck-runner-drools/README.md";

    public static void createTestResultsProperties() {
        final File testResultsPropertiesFile = new File(getTestResultsFolderPath() + "tck_results.properties");
        try {
            testResultsPropertiesFile.getParentFile().mkdirs();
            final boolean fileAlreadyExists = !testResultsPropertiesFile.createNewFile();
            if (fileAlreadyExists) {
                LOGGER.warn("tck_results.properties already exists!");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        updateTestResultsPropertiesFile(testResultsPropertiesFile);
    }

    public static String getTestResultsFilePath() {
        return getTestResultsFolderPath() + "tck_results.csv";
    }

    public static void deleteEverythingFromTestResultsFolder() {
        deleteAllFilesFromFolder(new File(TEST_RESULTS_FOLDER_PATH));
    }

    private static void updateTestResultsPropertiesFile(final File testResultsPropertiesFile) {
        final Properties testResultsProperties = loadTestResultsProperties(testResultsPropertiesFile);
        testResultsProperties.setProperty(PRODUCT_NAME_KEY, PRODUCT_NAME_VALUE);
        testResultsProperties.setProperty(PRODUCT_VERSION_KEY, DroolsPropertiesUtil.getDroolsVersion());
        testResultsProperties.setProperty(PRODUCT_COMMENT_KEY, PRODUCT_COMMENT_VALUE);
        testResultsProperties.setProperty(PRODUCT_URL_KEY, PRODUCT_URL_VALUE);
        testResultsProperties.setProperty(VENDOR_NAME_KEY, VENDOR_NAME_VALUE);
        testResultsProperties.setProperty(VENDOR_URL_KEY, VENDOR_URL_VALUE);
        testResultsProperties.setProperty(INSTRUCTIONS_URL_KEY, INSTRUCTIONS_URL_VALUE);
        testResultsProperties.setProperty(LAST_UPDATE_KEY, ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        try (final FileWriter fileWriter = new FileWriter(testResultsPropertiesFile)) {
            testResultsProperties.store(fileWriter, null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Properties loadTestResultsProperties(final File testResultsPropertiesFile) {
        final Properties testResultsProperties = new Properties();
        try {
            testResultsProperties.load(new FileReader(testResultsPropertiesFile));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return testResultsProperties;
    }

    private static String getTestResultsFolderPath() {
        return TEST_RESULTS_FOLDER_PATH + File.separator + DroolsPropertiesUtil.getDroolsVersion() + File.separator;
    }

    private static void deleteAllFilesFromFolder(final File folder) {
        final File[] filesInFolder = folder.listFiles(File::isDirectory);
        if (filesInFolder != null) {
            for (File file : filesInFolder) {
                final boolean deleted = file.delete();
                if (!deleted) {
                    LOGGER.warn("There was a problem deleting file: {}", file.getAbsolutePath());
                }
            }
        }
    }

    private TestResultsUtil() {
        // It is not allowed to create instances of util classes.
    }
}
