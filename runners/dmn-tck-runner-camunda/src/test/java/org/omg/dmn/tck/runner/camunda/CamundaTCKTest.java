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
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.camunda.dmn.DmnEngine;
import org.camunda.dmn.DmnEngine.EvalResult;
import org.camunda.dmn.DmnEngine.Failure;
import org.camunda.dmn.DmnEngine.Result;
import org.camunda.dmn.parser.ParsedDmn;
import org.camunda.feel.datatype.ZonedTime;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.omg.dmn.tck.marshaller._20160719.TestCases;
import org.omg.dmn.tck.marshaller._20160719.ValueType;
import org.omg.dmn.tck.marshaller._20160719.ValueType.Component;
import org.omg.dmn.tck.runner.junit4.DmnTckSuite;
import org.omg.dmn.tck.runner.junit4.DmnTckVendorTestSuite;
import org.omg.dmn.tck.runner.junit4.TestResult;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import scala.collection.JavaConverters;
import scala.util.Either;

@RunWith(DmnTckSuite.class)
public class CamundaTCKTest implements DmnTckVendorTestSuite {

	public static class CamundaContext implements TestSuiteContext {
		public DmnEngine engine;
		public ParsedDmn dmnModel;
	}

	@Override
	public String getResultFileName() {
		String resultDirectory = "../../TestResults/Camunda/7.9.0";
		new File(resultDirectory).mkdirs();
		return resultDirectory + "/" + DmnTckVendorTestSuite.super.getResultFileName();
	}

	private static final Logger logger = LoggerFactory.getLogger(CamundaTCKTest.class);
	private static final boolean LOG_DETAILS = false;
	public static final BigDecimal NUMBER_COMPARISON_PRECISION = new BigDecimal("0.00000001");

	@Override
	public List<URL> getTestCases() {
		List<URL> testCases = new ArrayList<>();
		File cl2parent = new File("../../TestCases/compliance-level-2");
		FilenameFilter filenameFilter = (dir, name) -> name.matches("\\d\\d\\d\\d-.*");
//		 FilenameFilter filenameFilter = (dir, inputName) -> inputName.matches( "000[1-4]-.*" );
//		 FilenameFilter filenameFilter = (dir, inputName) -> inputName.matches( "0004.*" );
		for (File file : cl2parent.listFiles(filenameFilter)) {
			try {
				testCases.add(file.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		File cl3parent = new File("../../TestCases/compliance-level-3");
		for (File file : cl3parent.listFiles(filenameFilter)) {
			try {
				testCases.add(file.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		java.util.Collections.sort(testCases, (URL url1, URL url2) -> url1.getPath().compareTo(url2.getPath()));
		return testCases;
	}

	@Override
	public TestSuiteContext createContext() {
		logger.info("Creating context.");
		final CamundaContext context = new CamundaContext();
		context.engine = new DmnEngine(new DmnEngine.Configuration(true, true));

		return context;
	}

	@Override
	public void beforeTestCases(TestSuiteContext context, TestCases testCases, URL modelURL) {
		logger.info("Creating runtime for model: {}\n", modelURL);
		CamundaContext ctx = (CamundaContext) context;

		try {
		  InputStream inputStream =  modelURL.openStream();

			final Either<Failure, ParsedDmn> parseResult = ctx.engine.parse(inputStream);

			if (parseResult.isLeft()) {
				final Failure failure = parseResult.left().get();
				throw new RuntimeException("Failed to parse DMN '" + modelURL + "': " + failure.message());
			} else {
				ctx.dmnModel = parseResult.right().get();
			}

		} catch (IOException e) {
			throw new RuntimeException("Failed to read DMN: " + modelURL, e);
		}
	}

	@Override
	public void beforeTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {
		// nothing to do
	}

	@Override
	public void afterTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {
		// nothing to do
	}

	@Override
	public void afterTestCase(TestSuiteContext context, TestCases testCases) {
		// nothing to do
	}

	@Override
	public TestResult executeTest(Description description, TestSuiteContext context, TestCases.TestCase testCase) {

		CamundaContext ctx = (CamundaContext) context;
		logger.info("Executing test '{} / {}'\n", description.getClassName(), description.getMethodName());

		Map<String, Object> variables = new HashMap<>();

		testCase.getInputNode().forEach(in -> {
		  variables.put(in.getName(), getValue(in));
		});

		List<String> failures = new ArrayList<>();
		testCase.getResultNode().forEach(rn -> {
			try {
				String name = rn.getName();

				Either<Failure, EvalResult> result = ctx.engine.evalByName(ctx.dmnModel, name, variables);

				Object expected = getValue(rn.getExpected());

				if (rn.isErrorResult()) {
					if (result.isRight() && (expected == null && !result.right().get().isNil())) {
						failures.add("FAILURE: '" + name + "' expected error but found='" + result.right().get() + "'");
					}
				} else {
					if (result.isLeft()) {
						failures.add(result.left().get().message());
					} else {
						EvalResult evalResult = result.right().get();

						Object actual = null;

						if (evalResult instanceof Result) {
							actual = ((Result) evalResult).value();
						}

						if (!isEquals(expected, actual)) {
							failures.add(
									"FAILURE: '" + name + "' expected='" + expected + "' but found='" + actual + "'");
						}
					}
				}

			} catch (Throwable t) {
				failures.add("FAILURE: unnexpected exception executing test case '" + description.getClassName() + " / "
						+ description.getMethodName() + "': " + t.getClass().getName());
				logger.error("FAILURE: unnexpected exception executing test case '{} / {}'", description.getClassName(),
						description.getMethodName(), t);
			}
		});

		TestResult.Result r = !failures.isEmpty() ? (LOG_DETAILS ? TestResult.Result.ERROR : TestResult.Result.IGNORED)
				: TestResult.Result.SUCCESS;
    // list of test cases that are unsupported by test runner or engine
    if (r == TestResult.Result.IGNORED) {
      switch (description.toString()) {
      case "001(0021-singleton-list-test-01)":
      case "_a217c840-6ead-4cb7-a3e1-ad9df9c0c584(0039-dt-list-semantics-test-01)":
      case "_07726176-a1a0-4e9e-9de2-16dba51556e2(0039-dt-list-semantics-test-01)":
        r = TestResult.Result.IGNORED;
        break;
      default:
        r = TestResult.Result.ERROR;
      }
    }
		return new TestResult(r, (LOG_DETAILS ? failures.stream().collect(Collectors.joining("; ")) : ""));
	}

	private Object getValue(ValueType valueType) {
		if (valueType.getValue() != null && !(valueType.getValue().getValue() instanceof Node)) {
			Object value = valueType.getValue().getValue();

			// transform XML date / time / duration types
			if (value instanceof XMLGregorianCalendar) {
				value = transformDateTime((XMLGregorianCalendar) value);
			} else if (value instanceof Duration) {
				value = transformDuration((Duration) value);
			}

			return value;
		}
		Object value = null;
		if (valueType.getValue() != null && ((Node) valueType.getValue().getValue()).getFirstChild() != null) {
			String text = ((Node) valueType.getValue().getValue()).getFirstChild().getTextContent();
			try {
				value = Long.valueOf(text);
			} catch (NumberFormatException e) {
				try {
					value = Double.valueOf(text);
				} catch (NumberFormatException e2) {
					boolean booleanValue = Boolean.valueOf(text);
					if (booleanValue || "false".equals(text)) {
						value = booleanValue;
					} else {
						value = text;
					}
				}
			}
		} else if (valueType.getList() != null) {
			List<Object> list = new ArrayList<>();
			for (ValueType item : valueType.getList().getValue().getItem()) {
				list.add(getValue(item));
			}
			value = list;
		} else if (value instanceof String[]) {
			List<String> list = new ArrayList<>();
			String[] array = (String[]) value;
			for (String string : array) {
				list.add(string);
			}
			value = list;
		} else if (valueType.getComponent() != null && !valueType.getComponent().isEmpty()) {
			Map<String, Object> context = new HashMap<>();
			for (Component component : valueType.getComponent()) {
				final Object compValue = getValue(component);
				context.put(component.getName(), compValue);

			}
			value = context;
		}
		return value;
	}

	private Object transformDateTime(final XMLGregorianCalendar cal) {
		Object result = null;

		final QName type = cal.getXMLSchemaType();

		final BigDecimal fractionalSecond = cal.getFractionalSecond();
		int nanoSeconds = 0;
		if (fractionalSecond != null) {
			nanoSeconds = fractionalSecond.movePointRight(9).intValue();
		}

		final int timezone = cal.getTimezone();
		final boolean hasOffset = timezone != DatatypeConstants.FIELD_UNDEFINED;
		final ZoneOffset offset = hasOffset ? ZoneOffset.ofTotalSeconds(timezone * 60) : null;

		if (type == DatatypeConstants.DATE) {
			result = LocalDate.of(cal.getYear(), cal.getMonth(), cal.getDay());
		} else if (type == DatatypeConstants.TIME) {
			final LocalTime localTime = LocalTime.of(cal.getHour(), cal.getMinute(), cal.getSecond(), nanoSeconds);
			result = localTime;

			if (hasOffset) {
				result = ZonedTime.of(localTime, offset);
			}
		} else if (type == DatatypeConstants.DATETIME) {
			final LocalDateTime locateDateTime = LocalDateTime.of(cal.getYear(), cal.getMonth(), cal.getDay(),
					cal.getHour(), cal.getMinute(), cal.getSecond(), nanoSeconds);
			result = locateDateTime;

			if (hasOffset) {
				result = OffsetDateTime.of(locateDateTime, offset).toZonedDateTime();
			}
		}
		return result;
	}

	private Object transformDuration(final Duration duration) {
		final boolean isYourMonthDuration = duration.isSet(DatatypeConstants.YEARS) || duration.isSet(DatatypeConstants.MONTHS);
		final boolean isNeg = duration.getSign() < 0;
		final int n = isNeg ? -1 : 1;

		if (isYourMonthDuration) {
			return Period.of(n * duration.getYears(), n * duration.getMonths(), n * duration.getDays());
		} else {
			long nanos = 0L;

			if (duration.isSet(DatatypeConstants.SECONDS))
			{
				final Number seconds = duration.getField(DatatypeConstants.SECONDS);
				final BigDecimal fractionSeconds = BigDecimal.valueOf(seconds.doubleValue()).remainder(BigDecimal.ONE);
				nanos = fractionSeconds.movePointRight(9).longValue();
			}

			return java.time.Duration.ofDays(n * duration.getDays()).plusHours(n * duration.getHours())
					.plusMinutes(n * duration.getMinutes()).plusSeconds(n * duration.getSeconds()).plusNanos(nanos);
		}
	}

	private boolean isEquals(Object expected, Object actual) {
		if (expected == actual) {
			// this includes both being null
			return true;
		}
		if ((expected == null && actual != null) || (expected != null && actual == null)) {
			return false;
		}
		if (expected instanceof Number && actual instanceof Number) {
			BigDecimal expectedBD = getBigDecimalOrNull(expected);
			BigDecimal actualBD = getBigDecimalOrNull(actual);
			return expectedBD.subtract(actualBD).abs().compareTo(NUMBER_COMPARISON_PRECISION) < 0;
		}
		if (actual instanceof scala.collection.immutable.List) {
			actual = JavaConverters.bufferAsJavaListConverter(((scala.collection.immutable.List) actual).toBuffer())
					.asJava();
		}
		if (actual instanceof scala.collection.immutable.Map) {
			actual = JavaConverters.mapAsJavaMapConverter((scala.collection.immutable.Map) actual).asJava();
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

	public static BigDecimal getBigDecimalOrNull(Object value) {
		if (!(value instanceof Number || value instanceof String)) {
			return null;
		}
		if (!BigDecimal.class.isAssignableFrom(value.getClass())) {
			if (value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte
					|| value instanceof AtomicLong || value instanceof AtomicInteger) {
				value = new BigDecimal(((Number) value).longValue(), MathContext.DECIMAL128);
			} else if (value instanceof BigInteger) {
				value = new BigDecimal(((BigInteger) value).toString(), MathContext.DECIMAL128);
			} else if (value instanceof String) {
				// we need to remove leading zeros to prevent octal conversion
				value = new BigDecimal(((String) value).replaceFirst("^0+(?!$)", ""), MathContext.DECIMAL128);
			} else {
				value = new BigDecimal(((Number) value).doubleValue(), MathContext.DECIMAL128);
			}
		}
		return (BigDecimal) value;
	}

}
