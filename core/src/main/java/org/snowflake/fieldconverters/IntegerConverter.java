package org.snowflake.fieldconverters;

public class IntegerConverter implements FieldConverter {

    @Override
    public boolean accepts(Class<?> type) {
        return type == int.class || type == Integer.class;
    }

    @Override
    public Object convert(String input, Class<?> type) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new FieldValidationException("Invalid integer \"" + input + "\"");
        }
    }

}
