package org.omg.dmn.tck.runner.drools;

import org.kie.dmn.feel.util.NumberEvalHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class CompareValuesUtil {

    public static final BigDecimal NUMBER_COMPARISON_PRECISION = new BigDecimal("0.00000001");

    public static boolean areEqual(Object object1, Object object2) {
        // This includes both being null.
        if (object1 == object2) {
            return true;
            // If one of those is null.
        } else if ((object1 == null) || (object2 == null)) {
            return false;
        } else if (!object1.getClass().isAssignableFrom(object2.getClass())) {
            return false;
        } else if (object1 instanceof Number && object2 instanceof Number) {
            BigDecimal expectedBD = NumberEvalHelper.getBigDecimalOrNull(object1);
            BigDecimal actualBD = NumberEvalHelper.getBigDecimalOrNull(object2);
            return expectedBD.subtract(actualBD).abs().compareTo(NUMBER_COMPARISON_PRECISION) < 0;
        } else if (object1 instanceof List && object2 instanceof List) {
            return areEqualLists((List<Object>) object1, (List<Object>) object2);
        } else if (object1 instanceof Map && object2 instanceof Map) {
            return areEqualMaps((Map<Object, Object>) object1, (Map<Object, Object>) object2);
        } else {
            return object1.equals(object2);
        }
    }

    private static boolean areEqualLists(List<Object> list1, List<Object> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if (!areEqual(list1.get(i), list2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean areEqualMaps(Map<Object, Object> map1, Map<Object, Object> map2) {
        if (map1.size() != map2.size()) {
            return false;
        }
        for (Map.Entry<Object, Object> entry : map1.entrySet()) {
            if (!areEqual(entry.getValue(), map2.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    private CompareValuesUtil() {
        // It is not allowed to create instances of util classes.
    }
}
