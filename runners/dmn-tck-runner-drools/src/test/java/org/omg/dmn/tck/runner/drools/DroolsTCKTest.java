/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.omg.dmn.tck.runner.drools;

import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.compiler.DMNTypeRegistryV12;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.util.NumberEvalHelper;
import org.kie.dmn.model.api.Definitions;
import org.kie.internal.utils.KieHelper;
import org.omg.dmn.tck.marshaller._20160719.TestCaseType;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.marshaller._20160719.ValueType;
import org.omg.dmn.tck.runner.junit4.DmnTckSuite;
import org.omg.dmn.tck.runner.junit4.DmnTckVendorTestSuite;
import org.omg.dmn.tck.runner.junit4.TestResult;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@RunWith(DmnTckSuite.class)
public class DroolsTCKTest implements DmnTckVendorTestSuite {

    private static final SimpleFileVisitor<Path> rmrf = new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };
    private static final Logger LOGGER = LoggerFactory.getLogger(DroolsTCKTest.class);
    public static final BigDecimal NUMBER_COMPARISON_PRECISION = new BigDecimal("0.00000001");

    private static final DMNTypeRegistryV12 REGISTRY = new DMNTypeRegistryV12();

    private File testResultsProperties;
    private Properties props;
    private String droolsVersion;

    {
        // TODO - this needs to be broken into some init() method or similar.
        try {
            droolsVersion = getDroolsVersion();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final File testResultsDirectory = new File("../../TestResults/Drools");
        // Delete everything in results.
        deleteAllFilesFromFolder(testResultsDirectory);

        // Create required files.
        testResultsProperties = new File("../../TestResults/Drools/" + droolsVersion + "/tck_results.properties");
        try {
            testResultsProperties.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDroolsVersion() throws IOException {
        final Properties ps = new Properties();
        ps.load(this.getClass().getResourceAsStream("/drools.properties"));
        final String droolsVersionFromProperties = ps.getProperty("drools.version");
        return droolsVersionFromProperties != null ? droolsVersionFromProperties : "";
    }

    private void deleteAllFilesFromFolder(final File folder) {
        final File[] filesInFolder = folder.listFiles(File::isDirectory);
        if (filesInFolder != null) {
            for (File file : filesInFolder) {
                final boolean deleted = file.delete();
                if (!deleted) {
                    LOGGER.warn("There was a problem deleting file: " + file.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public List<URL> getTestCases() {
        List<URL> testCases = new ArrayList<>();
        addTestCasesFolders(new File("../../TestCases/compliance-level-2"), testCases);
        addTestCasesFolders(new File("../../TestCases/compliance-level-3"), testCases);
        testCases.sort(Comparator.comparing(URL::toString));
        return testCases;
    }

    private void addTestCasesFolders(final File folder, final List<URL> testCases) {
        final File[] testCasesFolders = folder.listFiles(File::isDirectory);
        if (testCasesFolders != null) {
            for (File file : testCasesFolders) {
                try {
                    testCases.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    LOGGER.error("Cannot process test file URL: {}", file.toURI(), e);
                }
            }
        }
    }

    @Override
    public TestSuiteContext createContext() {
        LOGGER.info("Creating context.");
        return new DroolsContext();
    }

    @Override
    public void beforeTestCases(TestSuiteContext context, TestCases testCases, URL modelURL, Collection<? extends URL> additionalModels) {
        LOGGER.info("Creating runtime for model: {} and additional models {}\n", modelURL, additionalModels);
        DroolsContext ctx = (DroolsContext) context;
        ctx.runtime = createRuntime(modelURL, additionalModels);
        if (ctx.runtime.getModels().isEmpty()) {
            throw new RuntimeException("Unable to load model for URL '" + modelURL + "'");
        }
        try {
            Definitions mainModelXML = DMNMarshallerFactory.newDefaultMarshaller().unmarshal(new FileReader(modelURL.getFile()));
            ctx.dmnmodel = ctx.runtime.getModel(mainModelXML.getNamespace(), mainModelXML.getName());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to locate file for model for URL '" + modelURL + "'");
        }
    }

    @Override
    public void beforeTestCases(TestSuiteContext context, TestCases testCases, URL modelURL) {
        LOGGER.info("Creating runtime for model: {}\n", modelURL);
        DroolsContext ctx = (DroolsContext) context;
        ctx.runtime = createRuntime(modelURL, Collections.emptyList());
        if (ctx.runtime.getModels().isEmpty()) {
            throw new RuntimeException("Unable to load model for URL '" + modelURL + "'");
        }
        ctx.dmnmodel = ctx.runtime.getModels().get(0);
    }

    @Override
    public void beforeTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {
        // nothing to do
    }

    @Override
    public TestResult executeTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {
        DroolsContext ctx = (DroolsContext) context;
        LOGGER.info("Executing test '{} / {}'\n", description.getClassName(), description.getMethodName());

        DMNContext dmnctx = ctx.runtime.newContext();
        testCase.getInputNode().forEach(in -> {
            InputDataNode input = ctx.dmnmodel.getInputByName(in.getName());
            DecisionNode decision = ctx.dmnmodel.getDecisionByName(in.getName());
            if (input != null) {
                dmnctx.set(in.getName(), parseValue(in, input)); // normally data input from file, should be pointing at a InputData in the model
            } else if (decision != null) {
                dmnctx.set(in.getName(), parseValue(in, decision)); // the test case offers a pre-evaluated Decision
            } else {
                LOGGER.warn("Override input name {} with value {}", in.getName(), in);
                dmnctx.set(in.getName(), parseType(in, REGISTRY.unknown()));
            }
        });

        DMNContext resultctx = dmnctx;
        List<String> failures = new ArrayList<>();
        for (TestCases.TestCase.ResultNode rn : testCase.getResultNode()) {
            try {
                String name = rn.getName();
                DMNResult dmnResult;
                if (testCase.getType() == TestCaseType.DECISION_SERVICE) {
                    dmnResult = ctx.runtime.evaluateDecisionService(ctx.dmnmodel, resultctx, testCase.getInvocableName());
                } else {
                    dmnResult = ctx.runtime.evaluateByName(ctx.dmnmodel, resultctx, name);
                }
                if (!dmnResult.getMessages().isEmpty()) {
                    LOGGER.info("Messages: \n-----\n{}\n-----\n", dmnResult.getMessages().stream().map(Object::toString).collect(Collectors.joining("\n")));
                }
                resultctx = dmnResult.getContext();
                Object expected = parseValue(rn, ctx.dmnmodel.getDecisionByName(name));
                Object actual = resultctx.get(name);
                if (rn.isErrorResult()) {
                    for (DMNMessage msg : dmnResult.getMessages(DMNMessage.Severity.ERROR)) {
                        LOGGER.info("TEST CASE is error Result, message reported is to be expected: {}", msg);
                    }
                } else {
                    if (expected == null && actual == null) {
                        LOGGER.warn("TCK value comparison is correct, but TCK test case '{}' might be missing errorResult flag?", name);
                        LOGGER.warn("Here is a list of reported error messages:");
                        for (DMNMessage msg : dmnResult.getMessages(DMNMessage.Severity.ERROR)) {
                            LOGGER.warn(msg.getText());
                        }
                    } else if (dmnResult.hasErrors()) {
                        for (DMNMessage msg : dmnResult.getMessages(DMNMessage.Severity.ERROR)) {
                            failures.add(msg.toString());
                        }
                    }
                }
                if (!isEquals(expected, actual)) {
                    failures.add("FAILURE: '" + name + "' expected='" + expected + "' but found='" + actual + "'");
                }
            } catch (Throwable t) {
                final String failureString = String.format("FAILURE: unexpected exception executing test case '%s / %s'", description.getClassName(), description.getMethodName());
                failures.add(failureString);
                LOGGER.error(failureString, t);
            }
        }
        LOGGER.info("Result context: {}\n", resultctx);

        TestResult.Result r = !failures.isEmpty() ? TestResult.Result.ERROR : TestResult.Result.SUCCESS;
        return new TestResult(r, String.join("\n", failures));
    }

    private boolean isEquals(Object expected, Object actual) {
        // This includes both being null.
        if (expected == actual) {
            return true;
        }
        // If one of those is null.
        if ((expected == null) || (actual == null)) {
            return false;
        }
        if (expected instanceof Number && actual instanceof Number) {
            BigDecimal expectedBD = NumberEvalHelper.getBigDecimalOrNull(expected);
            BigDecimal actualBD = NumberEvalHelper.getBigDecimalOrNull(actual);
            return expectedBD.subtract(actualBD).abs().compareTo(NUMBER_COMPARISON_PRECISION) < 0;
        }
        if (expected instanceof List && actual instanceof List) {
            List e = (List) expected;
            List a = (List) actual;
            if (e.size() != a.size()) {
                return false;
            }
            for (int i = 0; i < e.size(); i++) {
                if (!isEquals(e.get(i), a.get(i))) {
                    return false;
                }
            }
            return true;
        }
        if (expected instanceof Map && actual instanceof Map) {
            Map<Object, Object> e = (Map<Object, Object>) expected;
            Map<Object, Object> a = (Map<Object, Object>) actual;
            if (e.size() != a.size()) {
                return false;
            }
            for (Map.Entry entry : e.entrySet()) {
                if (!isEquals(entry.getValue(), a.get(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        }
        if (!expected.getClass().isAssignableFrom(actual.getClass())) {
            return false;
        }
        return expected.equals(actual);
    }

    @Override
    public void afterTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {
        // nothing to do
    }

    @Override
    public void afterTestCase(TestSuiteContext context, TestCases testCases) {
        props.setProperty("last.update", ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        try {
            props.store(new FileOutputStream(testResultsProperties), null);
        } catch (IOException e) {
            LOGGER.warn("Exception while updating last update date", e);
        }
    }

    @Override
    public String getResultFileName() {
        return "../../TestResults/Drools/" + droolsVersion + "/tck_results.csv";
    }

    protected DMNRuntime createRuntime(URL modelUrl, Collection<? extends URL> additionalModels) {
        KieServices ks = KieServices.Factory.get();
        KieHelper kieHelper = new KieHelper().addResource(ks.getResources().newFileSystemResource(modelUrl.getFile()));
        for (URL a : additionalModels) {
            kieHelper.addResource(ks.getResources().newFileSystemResource(a.getFile()));
        }
        KieContainer kieContainer = kieHelper.getKieContainer();
        DMNRuntime runtime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);

        if (runtime == null) {
            throw new RuntimeException("Unable to create DMN Runtime");
        }
        return runtime;
    }

    private Object parseValue(TestCases.TestCase.InputNode in, InputDataNode input) {
        if (input == null || input.getType() == null) {
            throw new RuntimeException("Unknown type for input node " + in.getName());
        }
        return parseType(in, input.getType());
    }

    private Object parseValue(TestCases.TestCase.InputNode in, DecisionNode decision) {
        if (decision == null || decision.getResultType() == null) {
            throw new RuntimeException("Unknown type for decision node " + in.getName());
        }
        return parseType(in, decision.getResultType());
    }

    private Object parseValue(TestCases.TestCase.ResultNode rn, DecisionNode decision) {
        if (decision == null || decision.getResultType() == null) {
            throw new RuntimeException("Unknown type for input node " + rn.getName());
        }
        return parseType(rn.getExpected(), decision.getResultType());
    }

    private Object parseType(ValueType value, DMNType dmnType) {
        if (value.getList() != null && !value.getList().isNil()) {
            List<Object> result = new ArrayList<>();
            ValueType.List list = value.getList().getValue();
            for (ValueType vt : list.getItem()) {
                result.add(parseType(vt, (dmnType.isCollection()) ? dmnType : REGISTRY.unknown()));
            }
            return result;
        } else if (isDMNSimpleType(dmnType) || (isDMNAny(dmnType) && isJAXBValue(value) && !isJAXBComponent(value))) {
            String text = null;
            JAXBElement<Object> val = value.getValue();
            if (val != null && !value.getValue().isNil() && val.getValue() instanceof Node && !isDateTimeOrDuration(val.getValue())) {
                Node nodeVal = (Node) ((JAXBElement<?>) val).getValue();
                if (nodeVal.getFirstChild() != null) {
                    text = nodeVal.getFirstChild().getTextContent();
                }
                if (text != null) {
                    if (isDMNSimpleType(dmnType)) {
                        return recurseSimpleDMNTypeToFindBuiltInFEELType((BaseDMNTypeImpl) dmnType).fromString(text);
                    } else {
                        // no information from DMN; try to use the xml test case file.
                        Class<?> xmlClass = value.getValue().getDeclaredType();
                        if (!xmlClass.equals(Object.class) && !xmlClass.equals(String.class)) {
                            try {
                                Method m = xmlClass.getMethod("valueOf", String.class);
                                return m.invoke(null, text);
                            } catch (Exception e) {
                                return text;
                            }
                        } else {
                            return text;
                        }
                    }
                } else {
                    return null;
                }
            } else if (val != null && !(val.getValue() instanceof Node) && !isDateTimeOrDuration(val.getValue())) {
                return ((JAXBElement<?>) val).getValue();
            } else {
                try {
                    Object dateTimeOrDurationValue = (val != null) ? val.getValue() : null;
                    if (dateTimeOrDurationValue instanceof Duration || dateTimeOrDurationValue instanceof XMLGregorianCalendar) {
                        if (!isDMNAny(dmnType)) {
                            // need to convert to java.time.* equivalent
                            text = dateTimeOrDurationValue.toString();
                            return text != null ? recurseSimpleDMNTypeToFindBuiltInFEELType((BaseDMNTypeImpl) dmnType).fromString(text) : null;
                        } else {
                            // no DMN type information from the DMN model
                            if (dateTimeOrDurationValue instanceof Duration) {
                                try {
                                    return java.time.Duration.parse(dateTimeOrDurationValue.toString());
                                } catch (DateTimeParseException e) {
                                    return ComparablePeriod.parse(dateTimeOrDurationValue.toString());
                                }
                            } else {
                                XMLGregorianCalendar xmlCal = (XMLGregorianCalendar) dateTimeOrDurationValue;
                                if (xmlCal.getTimezone() != DatatypeConstants.FIELD_UNDEFINED && xmlCal.getXMLSchemaType() == DatatypeConstants.DATETIME) {
                                    return ZonedDateTime.parse(xmlCal.toXMLFormat());
                                } else if (xmlCal.getTimezone() != DatatypeConstants.FIELD_UNDEFINED && xmlCal.getXMLSchemaType() == DatatypeConstants.TIME) {
                                    return OffsetTime.parse(xmlCal.toXMLFormat());
                                } else if (xmlCal.getXMLSchemaType() == DatatypeConstants.DATETIME) {
                                    return LocalDateTime.of(LocalDate.of(xmlCal.getYear(), xmlCal.getMonth(), xmlCal.getDay()), LocalTime.of(xmlCal.getHour(), xmlCal.getMinute(), xmlCal.getSecond()));
                                } else if (xmlCal.getXMLSchemaType() == DatatypeConstants.DATE) {
                                    return LocalDate.of(xmlCal.getYear(), xmlCal.getMonth(), xmlCal.getDay());
                                } else if (xmlCal.getXMLSchemaType() == DatatypeConstants.TIME) {
                                    return LocalTime.parse(xmlCal.toXMLFormat());
                                }
                                return xmlCal.toGregorianCalendar();
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Error trying to coerce JAXB type {} with value '{}': {}", val.getClass().getName(), val, e.getMessage());
                }
                return val;
            }
        } else if (isDMNCompositeType(dmnType)) {
            Map<String, Object> result = new HashMap<>();
            if (value.getComponent().isEmpty()) {
                return null;
            }
            for (ValueType.Component component : value.getComponent()) {
                DMNType fieldType = dmnType.getFields().get(component.getName());
                if (!dmnType.getFields().containsKey(component.getName())) {
                    LOGGER.warn("Error TCK Runner type check: unknown field '{}' for type '{}'. This usually happens when a TCK test leverages coercion.", component.getName(), dmnType.getName());
                    fieldType = REGISTRY.unknown();
                }
                if (fieldType == null) {
                    throw new RuntimeException("Error parsing input: unknown type for field '" + component.getName() + "' on type " + dmnType.getName() + "'");
                }
                Object fieldValue = parseType(component, fieldType);
                result.put(component.getName(), fieldValue);
            }
            return result;
        } else if (isDMNAny(dmnType) && !isJAXBValue(value) && isJAXBComponent(value)) {
            Map<String, Object> result = new HashMap<>();
            for (ValueType.Component component : value.getComponent()) {
                Object fieldValue = parseType(component, REGISTRY.unknown());
                result.put(component.getName(), fieldValue);
            }
            return result;
        } else {
            throw new RuntimeException("Unable to infer information from JAXB type");
        }
    }

    private boolean isJAXBComponent(ValueType value) {
        return value.getComponent() != null && !value.getComponent().isEmpty();
    }

    private boolean isJAXBValue(ValueType value) {
        return value.getValue() != null;
    }

    private boolean isDMNAny(DMNType dmnType) {
        return ((BaseDMNTypeImpl) dmnType).getFeelType().equals(BuiltInType.UNKNOWN);
    }

    private boolean isDMNSimpleType(DMNType dmnType) {
        return !dmnType.isComposite() && !isDMNAny(dmnType);
    }

    private boolean isDMNCompositeType(DMNType dmnType) {
        return dmnType.isComposite() && !isDMNAny(dmnType);
    }

    private BuiltInType recurseSimpleDMNTypeToFindBuiltInFEELType(BaseDMNTypeImpl dmnType) {
        Type feelType = dmnType.getFeelType();
        if (feelType instanceof BuiltInType) {
            return (BuiltInType) feelType;
        } else if (dmnType instanceof SimpleTypeImpl) {
            // circumvent AliasFEELType..
            return recurseSimpleDMNTypeToFindBuiltInFEELType((BaseDMNTypeImpl) dmnType.getBaseType());
        } else {
            throw new RuntimeException("Unable to recurse to determine BuiltInType");
        }
    }

    private boolean isDateTimeOrDuration(Object value) {
        return value instanceof Duration || value instanceof XMLGregorianCalendar;
    }

    public static class DroolsContext implements TestSuiteContext {
        public DMNRuntime runtime;
        public DMNModel dmnmodel;
    }
}
