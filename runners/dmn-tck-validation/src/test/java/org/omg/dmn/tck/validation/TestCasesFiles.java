
package org.omg.dmn.tck.validation;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
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
    public void testCaseFileIsValid() throws Exception {
        for (File dmnFile : basedir.listFiles((dir, name) -> name.matches(".*\\.xml$"))) {
            testCasesSchema.newValidator().validate(new StreamSource(dmnFile));
        }
    }
}
