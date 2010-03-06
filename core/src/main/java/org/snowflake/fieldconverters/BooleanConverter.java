package org.snowflake.fieldconverters;

public class BooleanConverter implements FieldConverter {

    @Override
    public boolean accepts(Class<?> type) {
        return type == boolean.class || type == Boolean.class;
    }

    @Override
    public Object convert(String input, Class<?> type) throws FieldValidationException {
        if ("true".equalsIgnoreCase(input))
            return true;
        else
            return false;
    }

}
