package org.snowflake.fieldconverters;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateConverter implements FieldConverter {

    @Override
    public boolean accepts(Class<?> type) {
        return Date.class.isAssignableFrom(type);
    }

    @Override
    public Object convert(String input, Class<?> type) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        try {
            return df.parse(input);
        } catch (ParseException e) {
            // should never happen, as validate should have caught this
            throw new RuntimeException(e);
        }
    }

}
