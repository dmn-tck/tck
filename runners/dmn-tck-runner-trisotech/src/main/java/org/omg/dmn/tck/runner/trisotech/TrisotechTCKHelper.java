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

package org.omg.dmn.tck.runner.trisotech;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TrisotechTCKHelper {
    static private DocumentBuilder dBuilder;
    static {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            dBuilder = null;
        }
    }

    public static Properties getProperties() throws IOException {
        String userHome = System.getProperty("user.home");
        File configFile = new File(userHome, ".dmn-tck-runner-trisotech.properties");
        if (!configFile.exists()) {
            throw new FileNotFoundException("Could not find configuration file: " + configFile.getAbsolutePath());
        }

        Properties properties = new Properties();
        try (FileInputStream is = new FileInputStream(configFile)) {
            properties.load(is);
        }

        if (!properties.containsKey("url") || !properties.containsKey("bearer") || !properties.containsKey("repo")) {
            throw new IOException("Config file: " + configFile.getAbsolutePath() + " is incomplete");
        }
        if (!properties.containsKey("version")) {
            properties.put("version", "1.0");
        }
        return properties;

    }

    public static boolean pushTestCase(String groupId, String artifactId, Properties properties, File... dmnFiles)
            throws MalformedURLException, IOException, SAXException {

        String publishURL = properties.getProperty("url");
        if (!publishURL.endsWith("/")) {
            publishURL = publishURL + "/";
        }
        publishURL = publishURL + "execution/deployment/dmn/" + properties.getProperty("repo") + "/" + groupId + "/";

        String version = "/" + properties.getProperty("version");

        HttpURLConnection connection = (HttpURLConnection) new URL(publishURL + artifactId + version + "?strict=true").openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + properties.getProperty("bearer"));
        String boundary = "===" + UUID.randomUUID() + "===";
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        OutputStream output = connection.getOutputStream();
        for (File dmnFile : dmnFiles) {
            output.write(("--" + boundary + "\r\n").getBytes());
            output.write(("Content-Disposition: form-data; name=\"" + dmnFile.getName() + "\"; filename=\"" + dmnFile.getName() + "\"\r\n").getBytes());
            output.write("Content-Type: text/xml; charset=utf-8\r\n".getBytes());
            output.write("Content-Transfer-Encoding: binary\r\n".getBytes());
            output.write("\r\n".getBytes());
            Files.copy(dmnFile.toPath(), output);
            output.write("\r\n".getBytes());
        }
        output.write(("--" + boundary + "--\r\n").getBytes());

        output.flush();

        if (connection.getResponseCode() != 200) {
            connection.getInputStream().transferTo(System.out);
            throw new IOException("Could not deploy to the cloud: " + connection.getResponseCode() + " " + connection.getResponseMessage());
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        return true;
    }

    public static Map<String, Boolean> runTestCase(String groupId, String artifactId, File tckTestFile, Properties properties)
            throws MalformedURLException, IOException, SAXException {
        String publishURL = properties.getProperty("url");
        if (!publishURL.endsWith("/")) {
            publishURL += "/";
        }
        publishURL += "execution/dmn/api/test/" + properties.getProperty("repo") + "/" + groupId + "/";

        String version = "/" + properties.getProperty("version");
        HttpURLConnection connection = (HttpURLConnection) new URL(publishURL + artifactId + version).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + properties.getProperty("bearer"));
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

        OutputStream output = connection.getOutputStream();
        Files.copy(tckTestFile.toPath(), output);
        output.flush();

        LinkedHashMap<String, Boolean> results = new LinkedHashMap<String, Boolean>();

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (InputStream is = connection.getInputStream()) {
                Document xmlTestCases = dBuilder.parse(new InputSource(new InputStreamReader(is, StandardCharsets.UTF_8)));
                NodeList testCases = xmlTestCases.getElementsByTagName("testCase");
                for (int i = 0; i < testCases.getLength(); i++) {
                    Element testCase = (Element) testCases.item(i);
                    results.put(testCase.getAttribute("id"), "true".equals(testCase.getAttribute("passed")));
                }
            }

        }
        return results;

    }

}
