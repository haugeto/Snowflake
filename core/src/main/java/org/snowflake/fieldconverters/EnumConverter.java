package org.snowflake.fieldconverters;

public class EnumConverter implements FieldConverter {

    @Override
    public boolean accepts(Class<?> type) {
        return Enum.class.isAssignableFrom(type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convert(String input, Class<?> enumType) {
        Class<Enum> e = (Class<Enum>) enumType;
        try {
            return Enum.valueOf(e, input);
        } catch (IllegalArgumentException ex) {
            throw new FieldValidationException(ex.getMessage(), ex);
        }
    }

}
