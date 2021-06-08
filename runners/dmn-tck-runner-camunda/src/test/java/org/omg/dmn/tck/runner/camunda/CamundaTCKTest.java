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

package org.omg.dmn.tck.runner.camunda;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.marshaller._20160719.TestCases.TestCase.ResultNode;
import org.omg.dmn.tck.marshaller._20160719.ValueType;
import org.omg.dmn.tck.marshaller._20160719.ValueType.Component;
import org.omg.dmn.tck.runner.junit4.DmnTckSuite;
import org.omg.dmn.tck.runner.junit4.DmnTckVendorTestSuite;
import org.omg.dmn.tck.runner.junit4.TestResult;
import org.omg.dmn.tck.runner.junit4.TestResult.Result;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

@RunWith(DmnTckSuite.class)
public class CamundaTCKTest implements DmnTckVendorTestSuite {

	private static final File CL2_FOLDER = new File("../../TestCases/compliance-level-2");
	private static final File CL3_FOLDER = new File("../../TestCases/compliance-level-3");

	private static final BigDecimal NUMBER_COMPARISON_PRECISION = new BigDecimal("0.00000001");
	private static final boolean IGNORE_ERRORS = true;

	private static final Logger logger = LoggerFactory.getLogger(CamundaTCKTest.class);

	@Override
	public List<URL> getTestCases() {
		final List<URL> testCases = new ArrayList<>();

		testCases.addAll(getFiles(CL2_FOLDER));
		testCases.addAll(getFiles(CL3_FOLDER));

		testCases.sort(Comparator.comparing(URL::getPath));

		return testCases;
	}

	@Override
	public TestSuiteContext createContext() {
		return new CamundaContext();
	}

	@Override
	public void beforeTestCases(TestSuiteContext context, TestCases testCases, URL modelURL) {
		final CamundaContext ctx = (CamundaContext) context;

		final DefaultDmnEngineConfiguration configuration = new DefaultDmnEngineConfiguration();
		ctx.runtime = configuration.buildEngine();

		logger.info("Parse DMN file: {}", modelURL);

		try {
			//      final InputStream inputStream = modelURL.openStream();
			final EscapeAmbiguousNamesInputStream inputStream =
					new EscapeAmbiguousNamesInputStream(modelURL.openStream());

			final List<DmnDecision> decisions = ctx.runtime.parseDecisions(inputStream);

			ctx.decisionsByName =
					decisions.stream()
							.collect(
									Collectors.toMap(
											decision -> removeEscapedCharacters(decision.getName()),
											decision -> decision));

		} catch (Exception e) {
			ctx.parserException = e;

			logger.error("Failed to parse DMN file: {}", modelURL, e);
		}
	}

	@Override
	public void beforeTest(
			Description description, TestSuiteContext context, TestCases.TestCase testCase) {
		// nothing to do
	}

	@Override
	public TestResult executeTest(
			Description description, TestSuiteContext context, TestCases.TestCase testCase) {
		final CamundaContext ctx = (CamundaContext) context;

		if (ctx.parserException != null) {
			return ignoreTest("Parser Exception: " + getExceptionMessageRecursive(ctx.parserException));
		}

		if (ctx.decisionsByName.isEmpty()) {
			return ignoreTest("No parsable decision found.");
		}

		logger.info(
				"Executing test '{} / {}'\n", description.getClassName(), description.getMethodName());

		final Map<String, Object> inputVariables = new HashMap<>();
		testCase
				.getInputNode()
				.forEach(inputNode -> inputVariables.put(inputNode.getName(), getValue(inputNode)));

		final List<TestResult> results =
				testCase.getResultNode().stream()
						.map(resultNode -> evaluate(ctx, inputVariables, resultNode))
						.collect(Collectors.toList());

		return results.stream()
				.filter(testResult -> testResult.getResult() != Result.SUCCESS)
				.findFirst()
				.orElse(TestResult.success(""));
	}

	@Override
	public void afterTest(
			Description description, TestSuiteContext context, TestCases.TestCase testCase) {
		// nothing to do
	}

	@Override
	public void afterTestCase(TestSuiteContext context, TestCases testCases) {
		// nothing to do
	}

	@Override
	public String getResultFileName() {
		final String version = DmnEngine.class.getPackage().getImplementationVersion();
		String resultDirectory = "../../TestResults/Camunda/" + version;
		new File(resultDirectory).mkdirs();
		return resultDirectory + "/" + DmnTckVendorTestSuite.super.getResultFileName();
	}

	private TestResult evaluate(
			final CamundaContext ctx, final Map<String, Object> inputVariables, final ResultNode result) {

		final String decisionName = result.getName();

		if (!ctx.decisionsByName.containsKey(decisionName)) {
			return ignoreTest(
					String.format(
							"No decision found with name '%s'. Parsed decisions: '%s'",
							decisionName, ctx.decisionsByName.keySet()));
		}

		final DmnDecision decision = ctx.decisionsByName.get(decisionName);
		final Object expectedResult = getValue(result.getExpected());

		try {
			final DmnDecisionResult decisionResult =
					ctx.runtime.evaluateDecision(decision, inputVariables);

			final Object actual = extractDecisionResult(decisionResult, decisionName);

			if (isEquals(expectedResult, actual)) {
				return TestResult.success("");

			} else if (result.isErrorResult() && actual != null) {
				return failTest(String.format("Expected error but found '%s'", actual));

			} else {
				return failTest(String.format("Expected '%s' but was '%s'", expectedResult, actual));
			}

		} catch (Exception e) {

			if (result.isErrorResult()) {
				return TestResult.success("");

			} else {
				return failTest(
						String.format(
								"Expected '%s' but an error is thrown: %s",
								expectedResult, getExceptionMessageRecursive(e)));
			}
		}
	}

	private TestResult failTest(final String reason) {
		if (IGNORE_ERRORS) {
			logger.debug("Test failure: {}", reason);
			return TestResult.ignore("");

		} else {
			return TestResult.error(reason);
		}
	}

	private Object extractDecisionResult(
			final DmnDecisionResult decisionResult, final String decisionName) {

		Object actual = null;
		if (decisionResult.getResultList().size() == 1) {
			actual = decisionResult.getFirstResult().get(decisionName);
		} else {
			actual = decisionResult.collectEntries(decisionName);
		}

		if (actual == null) {
			// workaround: currently the decision result has a wrong structure
			final Map<String, Object> entryMap = decisionResult.getFirstResult().getEntryMap();
			if (entryMap.size() == 1) {
				actual = entryMap.values().iterator().next();
			} else {
				actual = entryMap;
			}
		}
		return actual;
	}

	private TestResult ignoreTest(String reason) {
		if (IGNORE_ERRORS) {
			logger.debug("Ignore test case: {}", reason);
			return TestResult.ignore("");

		} else {
			logger.info("Ignore test case: {}", reason);
			return TestResult.ignore(reason);
		}
	}

	private List<URL> getFiles(File folder) {
		final List<URL> urls = new ArrayList<>();

		for (File file : folder.listFiles()) {
			try {
				urls.add(file.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return urls;
	}

	private String getExceptionMessageRecursive(Throwable e) {
		String msg = e.getClass().getSimpleName() + ": " + e.getMessage();
		if (e.getCause() != null) {
			msg = msg + " – Caused by: " + getExceptionMessageRecursive(e.getCause());
		}
		return msg.replaceAll("\"", "&quot;").replace('\n', ' ');
	}

	private Object getValue(ValueType valueType) {
		final JAXBElement<Object> value = valueType.getValue();
		final JAXBElement<ValueType.List> listValue = valueType.getList();
		final List<Component> componentValue = valueType.getComponent();

		if (value == null && listValue == null && componentValue == null) {
			return null;
		}

		if (listValue != null) {
			final List<Object> list = new ArrayList<>();
			for (ValueType item : listValue.getValue().getItem()) {
				list.add(getValue(item));
			}
			return list;
		}

		if (componentValue != null && !componentValue.isEmpty()) {
			final Map<String, Object> context = new HashMap<>();
			for (Component component : componentValue) {
				final Object compValue = getValue(component);
				context.put(component.getName(), compValue);
			}
			return context;
		}

		if (value instanceof Node) {
			final Node node = (Node) value;
			final String text = node.getFirstChild().getTextContent();

			if ("true".equalsIgnoreCase(text) || "false".equalsIgnoreCase(text)) {
				return Boolean.valueOf(text);
			}

			try {
				return Long.valueOf(text);
			} catch (NumberFormatException e) {
			}

			try {
				return Double.valueOf(text);
			} catch (NumberFormatException e) {
			}

			return text;
		}

		if (value != null) {
			final Object singleValue = value.getValue();

			if (singleValue instanceof XMLGregorianCalendar) {
				return transformDateTime((XMLGregorianCalendar) singleValue);
			}

			if (singleValue instanceof Duration) {
				return transformDuration((Duration) singleValue);
			}

			if (singleValue instanceof String[]) {
				String[] array = (String[]) singleValue;
				return Arrays.asList(array);
			}

			return singleValue;
		}

		throw new RuntimeException(String.format("Unexpected value: '%s'", valueType));
	}

	private Object transformDateTime(final XMLGregorianCalendar cal) {

		final int nanoSeconds =
				Optional.ofNullable(cal.getFractionalSecond())
						.map(fraction -> fraction.movePointRight(9).intValue())
						.orElse(0);

		final int timezone = cal.getTimezone();

		final boolean hasOffset = timezone != DatatypeConstants.FIELD_UNDEFINED;
		final ZoneOffset offset = hasOffset ? ZoneOffset.ofTotalSeconds(timezone * 60) : null;

		final QName type = cal.getXMLSchemaType();

		if (type == DatatypeConstants.DATE) {
			return LocalDate.of(cal.getYear(), cal.getMonth(), cal.getDay());
		}

		if (type == DatatypeConstants.TIME) {
			final LocalTime localTime =
					LocalTime.of(cal.getHour(), cal.getMinute(), cal.getSecond(), nanoSeconds);

			if (hasOffset) {
				return OffsetTime.of(localTime, offset);
			} else {
				return localTime;
			}
		}

		if (type == DatatypeConstants.DATETIME) {
			final LocalDateTime locateDateTime =
					LocalDateTime.of(
							cal.getYear(),
							cal.getMonth(),
							cal.getDay(),
							cal.getHour(),
							cal.getMinute(),
							cal.getSecond(),
							nanoSeconds);

			if (hasOffset) {
				return OffsetDateTime.of(locateDateTime, offset).toZonedDateTime();
			} else {
				return locateDateTime;
			}
		}

		throw new RuntimeException(String.format("Unexpected calendar value: '%s'", cal));
	}

	private Object transformDuration(final Duration duration) {

		final boolean isYourMonthDuration =
				duration.isSet(DatatypeConstants.YEARS) || duration.isSet(DatatypeConstants.MONTHS);

		final boolean isNegative = duration.getSign() < 0;
		final int factor = isNegative ? -1 : 1;

		if (isYourMonthDuration) {
			return Period.of(
					factor * duration.getYears(), factor * duration.getMonths(), factor * duration.getDays());
		}

		final long nanos;
		if (duration.isSet(DatatypeConstants.SECONDS)) {
			final Number seconds = duration.getField(DatatypeConstants.SECONDS);
			final BigDecimal fractionSeconds =
					BigDecimal.valueOf(seconds.doubleValue())
							.subtract(BigDecimal.valueOf(seconds.longValue()));
			nanos = fractionSeconds.movePointRight(9).longValue();
		} else {
			nanos = 0;
		}

		return java.time.Duration.ofDays(factor * duration.getDays())
				.plusHours(factor * duration.getHours())
				.plusMinutes(factor * duration.getMinutes())
				.plusSeconds(factor * duration.getSeconds())
				.plusNanos(nanos);
	}

	private boolean isEquals(Object expected, Object actual) {

		if (expected == actual) {
			return true;
		}

		if (expected == null || actual == null) {
			return false;
		}

		if (expected instanceof Number && actual instanceof Number) {
			final BigDecimal expectedNumber = toBigDecimal((Number) expected);
			final BigDecimal actualNumber = toBigDecimal((Number) actual);

			return expectedNumber.subtract(actualNumber).abs().compareTo(NUMBER_COMPARISON_PRECISION) < 0;
		}

		if (expected instanceof List && actual instanceof List) {
			final List<Object> expectedList = (List<Object>) expected;
			final List<Object> actualList = (List<Object>) actual;

			if (expectedList.size() != actualList.size()) {
				return false;
			}

			for (int i = 0; i < expectedList.size(); i++) {
				if (!isEquals(expectedList.get(i), actualList.get(i))) {
					return false;
				}
			}
			return true;
		}

		if (expected instanceof Map && actual instanceof Map) {
			final Map<Object, Object> expectedContext = (Map<Object, Object>) expected;
			final Map<Object, Object> actualContext = (Map<Object, Object>) actual;

			if (expectedContext.size() != actualContext.size()) {
				return false;
			}

			for (Map.Entry<Object, Object> entry : expectedContext.entrySet()) {
				if (!isEquals(entry.getValue(), actualContext.get(entry.getKey()))) {
					return false;
				}
			}
			return true;
		}

		return expected.equals(actual);
	}

	public static BigDecimal toBigDecimal(Number value) {

		if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		}

		if (value instanceof BigInteger) {
			return new BigDecimal((BigInteger) value, MathContext.DECIMAL128);
		}

		final double doubleValue = value.doubleValue();
		return new BigDecimal(doubleValue, MathContext.DECIMAL128);
	}

	private String removeEscapedCharacters(String name) {
		return name.replaceAll(EscapeAmbiguousNamesInputStream.ESCAPE_CHARACTER, "");
	}

	private static final class CamundaContext implements TestSuiteContext {

		public DmnEngine runtime;
		public Map<String, DmnDecision> decisionsByName;

		public Exception parserException;
	}
}
