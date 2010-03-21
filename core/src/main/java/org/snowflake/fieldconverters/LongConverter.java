package org.snowflake.fieldconverters;

public class LongConverter implements FieldConverter {

    @Override
    public boolean accepts(Class<?> type) {
        return type == long.class || type == Long.class;
    }

    @Override
    public Object convert(String input, Class<?> type) throws FieldValidationException {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new FieldValidationException("Invalid long \"" + input + "\"");
        }
    }

}
