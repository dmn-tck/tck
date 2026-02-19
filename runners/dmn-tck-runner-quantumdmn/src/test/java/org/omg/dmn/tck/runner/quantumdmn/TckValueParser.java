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
import org.omg.dmn.tck.marshaller._20160719.ValueType;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses TCK XML ValueType elements into FeelValue objects for use with the QuantumDMN API.
 */
public final class TckValueParser {

    private TckValueParser() {
        // utility class
    }

    /**
     * Parses a TCK ValueType into a FeelValue.
     *
     * @param valueType the TCK value type to parse
     * @return the corresponding FeelValue, or null if the input is null
     */
    public static FeelValue parse(ValueType valueType) {
        if (valueType == null) {
            return FeelValue.ofNull();
        }

        // check for list
        JAXBElement<ValueType.List> listElement = valueType.getList();
        if (listElement != null && listElement.getValue() != null) {
            return parseList(listElement.getValue());
        }

        // check for components (context)
        List<ValueType.Component> components = valueType.getComponent();
        if (components != null && !components.isEmpty()) {
            return parseComponents(components);
        }

        // check for simple value
        JAXBElement<Object> valueElement = valueType.getValue();
        if (valueElement != null) {
            if (valueElement.isNil()) {
                return FeelValue.ofNull();
            }
            return parseSimpleValue(valueElement.getValue());
        }

        return FeelValue.ofNull();
    }

    private static FeelValue parseList(ValueType.List list) {
        List<FeelValue> items = new ArrayList<>();
        for (ValueType item : list.getItem()) {
            items.add(parse(item));
        }
        return FeelValue.ofList(items);
    }

    private static FeelValue parseComponents(List<ValueType.Component> components) {
        Map<String, FeelValue> context = new LinkedHashMap<>();
        for (ValueType.Component component : components) {
            String name = component.getName();
            FeelValue value = parse(component);
            context.put(name, value);
        }
        return FeelValue.ofContext(context);
    }

    private static FeelValue parseSimpleValue(Object value) {
        if (value == null) {
            return FeelValue.ofNull();
        }

        // handle XMLGregorianCalendar (dates/times)
        if (value instanceof XMLGregorianCalendar) {
            XMLGregorianCalendar cal = (XMLGregorianCalendar) value;
            return FeelValue.ofString(cal.toXMLFormat());
        }

        // handle Duration
        if (value instanceof Duration) {
            Duration duration = (Duration) value;
            return FeelValue.ofString(duration.toString());
        }

        // handle BigDecimal
        if (value instanceof BigDecimal) {
            return FeelValue.ofNumber((BigDecimal) value);
        }

        // handle other Number types
        if (value instanceof Number) {
            return FeelValue.ofNumber(new BigDecimal(value.toString()));
        }

        // handle Boolean
        if (value instanceof Boolean) {
            return FeelValue.ofBoolean((Boolean) value);
        }

        // handle String
        if (value instanceof String) {
            return parseTextValue((String) value);
        }

        // fallback - convert to string
        return FeelValue.ofString(value.toString());
    }

    private static FeelValue parseTextValue(String text) {
        if (text == null) {
            return FeelValue.ofNull();
        }

        // use trimmed value only for type detection, but preserve original for strings
        String trimmed = text.trim();
        
        // handle empty string (after trim check for type detection)
        if (trimmed.isEmpty()) {
            // preserve actual empty vs whitespace-only
            return FeelValue.ofString(text);
        }

        // try boolean (case insensitive, uses trimmed)
        if ("true".equalsIgnoreCase(trimmed)) {
            return FeelValue.ofBoolean(true);
        }
        if ("false".equalsIgnoreCase(trimmed)) {
            return FeelValue.ofBoolean(false);
        }
        // return as string - preserve original with whitespace
        return FeelValue.ofString(text);
    }
}
