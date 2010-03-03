package org.snowflake;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class InputOptions {

    final Map<String, String> nameValues = new LinkedHashMap<String, String>();

    String fieldName;

    public InputOptions(String fieldName) {
        this.fieldName = fieldName;
    }

    public InputOptions(String name, Set<String> values) {
        this(name);
        for (String value : values) {
            nameValues.put(value, value);
        }
    }

    public String put(String key, String value) {
        return nameValues.put(key, value);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Map<String, String> getNameValues() {
        return new LinkedHashMap<String, String>(nameValues);
    }

}
