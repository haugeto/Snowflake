package org.snowflake.fieldconverters;

public class StringConverter implements FieldConverter {

    public boolean accepts(Class<?> type) {
        return type == String.class;
    }

    public Object convert(String input, Class<?> type) {
        if (input != null) {
            if (input.isEmpty())
                return null;
        }
        return input;
    }

}
