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

package org.omg.dmn.tck.runner.trisotech.junit4;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.omg.dmn.tck.runner.trisotech.TrisotechTCKHelper;

@RunWith(Suite.class)
public class TrisotechTCKTestSuite extends Suite {

    protected List<Runner> runners = new LinkedList<Runner>();

    private Description description;

    public TrisotechTCKTestSuite(Class<?> clazz) throws InitializationError {
        super(clazz, Collections.<Runner> emptyList());

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
        } };

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier validHosts = new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        };
        // All hosts will be valid
        HttpsURLConnection.setDefaultHostnameVerifier(validHosts);
        Properties properties;
        try {
            properties = TrisotechTCKHelper.getProperties();
        } catch (IOException e) {
            throw new InitializationError(e);
        }

        File testCasesFolder = new File("../../TestCases");
        if (!testCasesFolder.exists()) {
            throw new InitializationError("Invalid DMN TCK test case folder: " + testCasesFolder.getAbsolutePath() + " it does not exist");
        }

        if (!testCasesFolder.isDirectory()) {
            throw new InitializationError("Invalid DMN TCK test case folder: " + testCasesFolder.getAbsolutePath() + " it's not a folder");
        }

        // Detect all test categories
        File[] testCategoriesFolders = testCasesFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (!dir.isDirectory()) {
                    return false;
                }
                return runTestGroup(name);
            }
        });

        description = Description.createSuiteDescription("DMN TCK test suite");

        File resultCSV = new File("target/tck_results.csv");
        if (resultCSV.exists()) {
            resultCSV.delete();
        }

        // Inside each category, there are a list of tests
        for (File testCategoriesFolder : testCategoriesFolders) {
            String category = testCategoriesFolder.getName();

            File[] testFolders = testCategoriesFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && runTest(category, pathname.getName());
                }
            });

            for (File testFolder : testFolders) {
                String testId = testFolder.getName();

                File[] testCasesFiles = testFolder.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        return name.startsWith(testId + "-test-") && name.endsWith(".xml")
                                && runTestCase(category, testId, name.substring(0, name.length() - 4));
                    }

                });

                for (File testCaseFile : testCasesFiles) {
                    String testCaseName = testCaseFile.getName().substring(0, testCaseFile.getName().length() - 4);

                    @SuppressWarnings("unchecked")
                    TrisotechTCKTestRunner runner = new TrisotechTCKTestRunner(properties, (Class<TrisotechTCKTest>) clazz, category, testId, testCaseName,
                            testCaseFile, resultCSV);

                    description.addChild(runner.getDescription());
                    runners.add(runner);
                }
            }
        }

    }

    protected boolean runTestGroup(String groupFolderName) {
        return groupFolderName.startsWith("compliance-");
    }

    protected boolean runTest(String groupFolderName, String testName) {
        return true;
    }

    private boolean runTestCase(String category, String testId, String testName) {
        return true;
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @Override
    public Description getDescription() {
        return description;
    }

}
