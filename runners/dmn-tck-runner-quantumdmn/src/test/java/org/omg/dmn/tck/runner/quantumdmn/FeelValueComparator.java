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
package org.omg.dmn.tck.runner.quantumdmn;

import com.quantumdmn.client.model.FeelValue;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Compares two FeelValue instances with precision tolerance for numbers.
 */
public final class FeelValueComparator {

    public static final BigDecimal NUMBER_PRECISION = new BigDecimal("0.00000001");

    private FeelValueComparator() {
    }

    /**
     * Compares two FeelValue instances for equality.
     * Numbers are compared with a precision tolerance.
     * Lists and contexts are compared recursively.
     *
     * @param expected the expected value
     * @param actual the actual value
     * @return true if the values are equal, false otherwise
     */
    public static boolean areEqual(FeelValue expected, FeelValue actual) {
        // Prepare null checks
        boolean expectedIsNull = (expected == null) || expected.isNull();
        boolean actualIsNull = (actual == null) || actual.isNull();

        if (expectedIsNull && actualIsNull) {
            return true;
        }
        if (expectedIsNull || actualIsNull) {
            return false;
        }

        // Compare by type
        if (expected.isNumber() && actual.isNumber()) {
            return areNumbersEqual(expected.asNumber(), actual.asNumber());
        }

        if (expected.isString() && actual.isString()) {
            return areStringsEqual(expected.asString(), actual.asString());
        }

        if (expected.isBoolean() && actual.isBoolean()) {
            return Objects.equals(expected.asBoolean(), actual.asBoolean());
        }

        if (expected.isList() && actual.isList()) {
            return areListsEqual(expected.asList(), actual.asList());
        }

        if (expected.isContext() && actual.isContext()) {
            return areContextsEqual(expected.asContext(), actual.asContext());
        }

        // --- Handle Type mismatches (e.g. backend api (json) returns String for Number) ---

        // expected Number, actual String
        if (expected.isNumber() && actual.isString()) {
            return attemptStringAsNumber(expected.asNumber(), actual.asString());
        }

        // expected Boolean, actual String
        if (expected.isBoolean() && actual.isString()) {
            return attemptStringAsBoolean(expected.asBoolean(), actual.asString());
        }

        // expected String, actual Number (reverse case)
        if (expected.isString() && actual.isNumber()) {
            return attemptStringAsNumber(actual.asNumber(), expected.asString());
        }

        // type mismatch - try to compare as raw values
        return Objects.equals(expected.getRawValue(), actual.getRawValue());
    }

    private static boolean areStringsEqual(String expected, String actual) {
        if (Objects.equals(expected, actual)) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }

        // normalize timezone representations: Z is equivalent to +00:00
        String normalizedExpected = normalizeTimezone(expected);
        String normalizedActual = normalizeTimezone(actual);

        if (normalizedExpected.equals(normalizedActual)) {
            return true;
        }

        // compare as durations
        return areDurationsEqual(expected, actual);
    }

    private static String normalizeTimezone(String value) {
        // replace Z with +00:00 for comparison (both represent UTC)
        if (value.endsWith("Z")) {
            return value.substring(0, value.length() - 1) + "+00:00";
        }
        return value;
    }

    private static boolean areDurationsEqual(String expected, String actual) {
        // Try as java.time.Duration (DayTimeDuration)
        try {
            Duration d1 = Duration.parse(expected);
            Duration d2 = Duration.parse(actual);
            return d1.equals(d2);
        } catch (DateTimeParseException e) {
            // ignore
        }

        // Try as java.time.Period (YearMonthDuration)
        try {
            Period p1 = Period.parse(expected);
            Period p2 = Period.parse(actual);
            return p1.normalized().equals(p2.normalized());
        } catch (DateTimeParseException e) {
            // ignore
        }

        // Check for Zero equivalence across types (e.g. P0Y vs PT0S) if parsing failed for one or resulted in mixed types
        if (isZeroDuration(expected) && isZeroDuration(actual)) {
            return true;
        }

        // Try precision trimming if standard checks failed
        return checkDurationWithTrimming(expected, actual);
    }

    private static boolean checkDurationWithTrimming(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        if (!expected.endsWith("S") || !actual.endsWith("S")) {
            return false;
        }

        try {
            // Extract seconds parts
            int expSecStart = findSecondsStart(expected);
            int actSecStart = findSecondsStart(actual);

            if (expSecStart < 0 || actSecStart < 0) {
                return false;
            }

            String expSecStr = expected.substring(expSecStart, expected.length() - 1);
            String actSecStr = actual.substring(actSecStart, actual.length() - 1);

            BigDecimal expSec = new BigDecimal(expSecStr);
            BigDecimal actSec = new BigDecimal(actSecStr);

            // Check if actual has higher precision
            if (actSec.scale() > expSec.scale()) {
                // Trim actual to expected precision
                BigDecimal actSecTrimmed = actSec.setScale(expSec.scale(), java.math.RoundingMode.DOWN);

                // Reconstruct actual string with trimmed seconds
                String newActual = actual.substring(0, actSecStart) + actSecTrimmed.toPlainString() + "S";
                
                // Compare using Duration parsing
                try {
                    Duration d1 = Duration.parse(expected);
                    Duration d2 = Duration.parse(newActual);
                    return d1.equals(d2);
                } catch (Exception e) {
                    return false;
                }
            }
        } catch (Exception e) {
            // ignore parsing errors
        }
        return false;
    }

    private static int findSecondsStart(String duration) {
        // Scan backwards from 'S'
        for (int i = duration.length() - 2; i >= 0; i--) {
            char c = duration.charAt(i);
            if (!Character.isDigit(c) && c != '.') {
                return i + 1;
            }
        }
        return -1;
    }

    private static boolean isZeroDuration(String value) {
        try {
            return Duration.parse(value).isZero();
        } catch (DateTimeParseException e) {
            // try Period
        }
        try {
            return Period.parse(value).isZero();
        } catch (DateTimeParseException e) {
            // not a valid duration
        }
        return false;
    }

    private static boolean areNumbersEqual(BigDecimal expected, BigDecimal actual) {
        if (expected == null && actual == null) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        // compare with precision
        return expected.subtract(actual).abs().compareTo(NUMBER_PRECISION) < 0;
    }

    private static boolean attemptStringAsNumber(BigDecimal expected, String actual) {
        try {
            BigDecimal actualNum = new BigDecimal(actual);
            return areNumbersEqual(expected, actualNum);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean attemptStringAsBoolean(Boolean expected, String actual) {
        if ("true".equalsIgnoreCase(actual)) {
            return Boolean.TRUE.equals(expected);
        } else if ("false".equalsIgnoreCase(actual)) {
            return Boolean.FALSE.equals(expected);
        }
        return false;
    }

    private static boolean areListsEqual(List<FeelValue> expected, List<FeelValue> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }
        for (int i = 0; i < expected.size(); i++) {
            if (!areEqual(expected.get(i), actual.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean areContextsEqual(Map<String, FeelValue> expected, Map<String, FeelValue> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }
        for (Map.Entry<String, FeelValue> entry : expected.entrySet()) {
            FeelValue actualValue = actual.get(entry.getKey());
            if (!areEqual(entry.getValue(), actualValue)) {
                return false;
            }
        }
        return true;
    }
}
