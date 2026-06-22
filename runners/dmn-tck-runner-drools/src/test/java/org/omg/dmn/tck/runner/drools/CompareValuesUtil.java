package org.omg.dmn.tck.runner.drools;

import org.kie.dmn.feel.util.NumberEvalHelper;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public final class CompareValuesUtil {

    public static final BigDecimal NUMBER_COMPARISON_PRECISION = new BigDecimal("0.00000001");

    public static boolean areEqual(Object object1, Object object2) {
        Map<BiPredicate<Object, Object>, BiFunction<Object, Object, Boolean>> comparators = getEqualityComparators();
        
        for (Map.Entry<BiPredicate<Object, Object>, BiFunction<Object, Object, Boolean>> entry : comparators.entrySet()) {
            if (entry.getKey().test(object1, object2)) {
                return entry.getValue().apply(object1, object2);
            }
        }
        return Objects.equals(object1, object2);
    }

    private static Map<BiPredicate<Object, Object>, BiFunction<Object, Object, Boolean>> getEqualityComparators() {
        Map<BiPredicate<Object, Object>, BiFunction<Object, Object, Boolean>> map = new LinkedHashMap<>();
        
        map.put((o1, o2) -> o1 == o2, (o1, o2) -> true);
        
        map.put((o1, o2) -> o1 == null || o2 == null, (o1, o2) -> false);
        
        map.put(
            (o1, o2) -> o1 instanceof Number && o2 instanceof Number,
            (o1, o2) -> {
                BigDecimal expectedBD = NumberEvalHelper.getBigDecimalOrNull(o1);
                BigDecimal actualBD = NumberEvalHelper.getBigDecimalOrNull(o2);
                return expectedBD.subtract(actualBD).abs().compareTo(NUMBER_COMPARISON_PRECISION) < 0;
            }
        );
        
        map.put(
            (o1, o2) -> o1 instanceof List && o2 instanceof List,
            (o1, o2) -> areEqualLists((List<Object>) o1, (List<Object>) o2));

        map.put(
            (o1, o2) -> o1 instanceof Map && o2 instanceof Map,
            (o1, o2) -> areEqualMaps((Map<Object, Object>) o1, (Map<Object, Object>) o2)
        );

        map.put(
            (o1, o2) -> o1 instanceof String || o2 instanceof String,
            (o1, o2) -> o1.toString().equals(o2.toString()));

        map.put(
            (o1, o2) -> !o1.getClass().isAssignableFrom(o2.getClass()),
            (o1, o2) -> false
        );
        
        return map;
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
