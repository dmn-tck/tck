
package org.omg.dmn.tck.validation;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

@RunWith(Parameterized.class)
public class TestCasesFiles {

    static Schema dmnSchema;
    static Schema testCasesSchema;
    static {
        try {
            dmnSchema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File("../../TestCases/DMN12.xsd"));
            testCasesSchema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File("../../TestCases/testCases.xsd"));
        } catch (SAXException e) {
            throw new RuntimeException("Unable to initialize correctly.", e);
        }
    }

    @Parameters(name = "TestCase {0}")
    public static Iterable<Object[]> data() {
        List<Object[]> testCases = new ArrayList<>();
        File cl2parent = new File("../../TestCases/compliance-level-2");
        FileFilter filenameFilter = pathname -> pathname.isDirectory();
        for (File file : cl2parent.listFiles(filenameFilter)) {
            testCases.add(new Object[]{file.getName(), file});
        }
        File cl3parent = new File("../../TestCases/compliance-level-3");
        for (File file : cl3parent.listFiles(filenameFilter)) {
            testCases.add(new Object[]{file.getName(), file});
        }
        return testCases;
    }

    private String testCaseID;

    private File basedir;

    public TestCasesFiles(String testCaseID, File basedir) {
        this.testCaseID = testCaseID;
        this.basedir = basedir;
    }

    @Test
    public void testDMNFileIsValid() throws Exception {
        for (File dmnFile : basedir.listFiles((dir, name) -> name.matches(".*\\.dmn$"))) {
            dmnSchema.newValidator().validate(new StreamSource(dmnFile));
        }
    }

    @Test
    public void testDMNFilesUniqueInNamespace() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Map<String, List<File>> namespaceToFiles = new HashMap<>();
        for (File dmnFile : basedir.listFiles((dir, name) -> name.matches(".*\\.dmn$"))) {
            Document document = builder.parse(dmnFile);
            Element definitions = document.getDocumentElement();
            String dmnNamespace = definitions.getAttribute("namespace"); // DMN specification, Definitions element, namespace attribute;
            namespaceToFiles.computeIfAbsent(dmnNamespace, x -> new ArrayList<>()).add(dmnFile);
        }
        for (Entry<String, List<File>> e : namespaceToFiles.entrySet()) {
            if (e.getValue().size() > 1) {
                throw new RuntimeException("There are 2 or more files with the same DMN namespace: " +
                                           e.getKey() +
                                           "\nin the folder: " +
                                           basedir +
                                           "\nthese files are: " +
                                           e.getValue());
            }
        }
    }

    @Test
    public void testCaseFileIsValid() throws Exception {
        for (File dmnFile : basedir.listFiles((dir, name) -> name.matches(".*\\.xml$"))) {
            testCasesSchema.newValidator().validate(new StreamSource(dmnFile));
        }
    }
}
