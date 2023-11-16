
package org.omg.dmn.tck.validation;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omg.dmn.tck.marshaller.TckMarshallingHelper;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class TestCasesFiles {

    static Schema dmnSchema;
    static Schema testCasesSchema;
    static {
        try {
            dmnSchema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(new Source[]{new StreamSource(new File("../../TestCases/DC.xsd")),
                                            new StreamSource(new File("../../TestCases/DI.xsd")),
                                            new StreamSource(new File("../../TestCases/DMNDI13.xsd")),
                                            new StreamSource(new File("../../TestCases/DMN14.xsd"))
                    });
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

    private static Node extractChild(Node parent, String tag) {
        if (parent == null) {
            return null;
        }
        NodeList childNodes = parent.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (tag.equals(item.getNodeName())) {
                return item;
            }
        }
        return null;
    }

    private static String extractStringAttribute(Node node, String attributeName) {
        if (node == null) {
            return null;
        }
        NamedNodeMap attributes = node.getAttributes();
        Node attribute = attributes.getNamedItem(attributeName);
        String value = null;
        if (attribute != null ) {
            value = attribute.getNodeValue();
        }
        return value;
    }

    private String testCaseID;

    private File basedir;
    private XPath xPath = XPathFactory.newInstance().newXPath();

    private XPathExpression input;
    private XPathExpression decision;
    private XPathExpression bkm;
    private XPathExpression ds;

    private XPathExpression inputDataVarMissingTypeRef;
    private XPathExpression bkmVariable;
    private XPathExpression bkmEncapsulatedLogic;
    /**
     * Spec 7.3.3 ItemDefinition metamodel, except `Any`.
     */
    private static final List<String> BUILTIN_NAMES_EXCEPT_ANY = Arrays.asList("number", "string", "boolean",
            "days and time duration", "years and months duration", "date", "time", "date and time");
    private DocumentBuilder builder;

    public TestCasesFiles(String testCaseID, File basedir) {
        this.testCaseID = testCaseID;
        this.basedir = basedir;
        try {
            input = xPath.compile("/*[local-name()='definitions']/*[local-name()='inputData']");
            decision = xPath.compile("/*[local-name()='definitions']/*[local-name()='decision']");
            bkm = xPath.compile("/*[local-name()='definitions']/*[local-name()='businessKnowledgeModel']");
            ds = xPath.compile("/*[local-name()='definitions']/*[local-name()='decisionService']");

            inputDataVarMissingTypeRef = xPath.compile("/*[local-name()='definitions']/*[local-name()='inputData']/*[local-name()='variable' and not(@*[local-name()='typeRef'])]/../@*[local-name()='name']");
            bkmVariable = xPath.compile("/*[local-name()='definitions']/*[local-name()='businessKnowledgeModel']/*[local-name()='variable']");
            bkmEncapsulatedLogic = xPath.compile("/*[local-name()='definitions']/*[local-name()='businessKnowledgeModel']/*[local-name()='encapsulatedLogic']");
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException("This JDK does not correctly support XPath", e);
        }
    }

    @Test
    public void testDMNFileIsValid() throws Exception {
        for (File dmnFile : basedir.listFiles((dir, name) -> name.matches(".*\\.dmn$"))) {
            Validator validator = dmnSchema.newValidator();
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            validator.validate(new StreamSource(dmnFile));
            checkDRGElementVariable(dmnFile);
            checkInputDataHasTypeRef(dmnFile);
            checkBKMTypeRef(dmnFile);
        }
    }

    private void checkBKMTypeRef(File dmnFile) throws Exception {
        Document xmlDocument = builder.parse(dmnFile);
        List<String> problems = new ArrayList<>();
        problems.addAll(checkBKMXPathTypeRef(dmnFile, xmlDocument, bkmVariable, "variable", "name"));
        problems.addAll(checkBKMXPathTypeRef(dmnFile, xmlDocument, bkmEncapsulatedLogic, "encapsulatedLogic", "id"));
        if (!problems.isEmpty()) {
            throw new RuntimeException(problems.stream().collect(Collectors.joining(", ")));
        }
    }

    private List<String> checkBKMXPathTypeRef(File dmnFile, Document xmlDocument, XPathExpression xpath, String elementName, String extractor) throws Exception {
        List<String> problems = new ArrayList<>();
        NodeList xPathVariables = (NodeList) xpath.evaluate(xmlDocument, XPathConstants.NODESET);
        if (xPathVariables.getLength() > 0) {
            for (int i = 0; i < xPathVariables.getLength(); i++) {
                Node variableNode = xPathVariables.item(i);
                NamedNodeMap variableAttributes = variableNode.getAttributes();
                Node typeRefAttribute = variableAttributes.getNamedItem("typeRef");
                if (typeRefAttribute != null ) {
                    String typeRefValue = typeRefAttribute.getNodeValue();
                    if (BUILTIN_NAMES_EXCEPT_ANY.contains(typeRefValue)) {                    
                        String problem = String.format("%s: BKM node %s '%s' typeRef is not a function (detected as: '%s' and expecting a function type instead)",
                                                       dmnFile.getName(),
                                                       elementName,
                                                       variableAttributes.getNamedItem(extractor),
                                                       typeRefValue);
                        System.err.println(problem);
                        problems.add(problem);
                    }                    
                }
            }
        }
        return problems;
    }

    private void checkInputDataHasTypeRef(File dmnFile) throws Exception {
        Document xmlDocument = builder.parse(dmnFile);
        NodeList xpathResults = (NodeList) inputDataVarMissingTypeRef.evaluate(xmlDocument, XPathConstants.NODESET);
        List<String> problems = new ArrayList<>();
        if (xpathResults.getLength() > 0) {
            for(int i = 0; i< xpathResults.getLength(); i++) {
                Node item = xpathResults.item(i);
                String problem = String.format("%s: InputData node '%s' variable missing typeRef", dmnFile.getName(), item);
                System.err.println(problem);
                problems.add(problem);
            }
        }
        if (!problems.isEmpty()) {
            throw new RuntimeException(problems.stream().collect(Collectors.joining(", "))+". Consider explicity typeRef for the specific test case value or typeRef='Any' if necessary.");
        }
    }

    // DRGElement.name == DRGElement.variable.name and DRGElement.typeRef == DRGElement.variable.typeRef
    private void checkDRGElementVariable(File dmnFile) throws Exception {
        Document xmlDocument = builder.parse(dmnFile);
        List<String> problems = new ArrayList<>();
        problems.addAll(checkDRGElementVariable(dmnFile, xmlDocument, input, "input"));
        problems.addAll(checkDRGElementVariable(dmnFile, xmlDocument, decision, "decision"));
        problems.addAll(checkDRGElementVariable(dmnFile, xmlDocument, bkm, "businessKnowledgeModel"));
        problems.addAll(checkDRGElementVariable(dmnFile, xmlDocument, ds, "decisionService"));
        if (!problems.isEmpty()) {
            throw new RuntimeException(problems.stream().collect(Collectors.joining(", ")));
        }
    }

    private List<String> checkDRGElementVariable(File dmnFile, Document xmlDocument, XPathExpression drgElementPath, String elementName) throws Exception {
        List<String> problems = new ArrayList<>();
        // For every DRG Element
        NodeList xPathVariables = (NodeList) drgElementPath.evaluate(xmlDocument, XPathConstants.NODESET);
        if (xPathVariables.getLength() > 0) {
            for (int i = 0; i < xPathVariables.getLength(); i++) {
                Node drgElementNode = xPathVariables.item(i);
                // Extract and compare values
                String name = extractStringAttribute(drgElementNode, "name");
                String typeRef = extractStringAttribute(drgElementNode, "typeRef");
                Node variableNode = extractChild(drgElementNode, "variable");
                String variableName = extractStringAttribute(variableNode, "name");
                String variableTypeRef = extractStringAttribute(variableNode, "typeRef");
                // Check name
                if (name != null && variableName != null) {
                    if (!name.equals(variableName)) {
                        String problem = String.format("%s: DRGElement node %s name '%s' is the same with the variable name '%s'",
                                dmnFile.getName(),
                                elementName,
                                name,
                                variableName);
                        System.err.println(problem);
                        problems.add(problem);
                    }
                }
                // Check typeRef
                if (typeRef != null && variableTypeRef != null) {
                    if (!typeRef.equals(variableTypeRef)) {
                        String problem = String.format("%s: DRGElement node %s typeRef '%s' is the same with the variable typeRef '%s'",
                                dmnFile.getName(),
                                elementName,
                                name,
                                variableName);
                        System.err.println(problem);
                        problems.add(problem);
                    }
                }
            }
        }
        return problems;
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
            Validator validator = testCasesSchema.newValidator();
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            validator.validate(new StreamSource(dmnFile));

            TestCases testCases = TckMarshallingHelper.load(new FileInputStream(dmnFile));
            Map<String, Long> idCounting = testCases.getTestCase()
                                                    .stream()
                                                    .map(TestCases.TestCase::getId)
                                                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            for (Entry<String, Long> kv : idCounting.entrySet()) {
                assertThat("The id '" + kv.getKey() + "' is occuring more than one time in the file " + dmnFile, kv.getValue(), is(1L));
            }
        }
    }
}
