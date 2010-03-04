package org.snowflake.fieldconverters;

/**
 * Enables custom conversion of user input.
 * 
 * @author haugeto
 */
public interface FieldConverter {

    public static final FieldConverter[] DEFAULT_CONVERTERS = { new StringConverter(), new IntegerConverter(),
            new BooleanConverter(), new EnumConverter(), new DateConverter() };

    /**
     * Tell whether this FieldConverter can handle the specified type
     */
    public boolean accepts(Class<?> type);

    /**
     * Convert the given input
     * 
     * @param input
     *            Raw user input
     * @param type
     *            Type to convert to
     * @return Converted value
     * @throws FieldValidationException
     *             If input cannot be converted (i.e. illegal value given)
     */
    public Object convert(String input, Class<?> type) throws FieldValidationException;

}