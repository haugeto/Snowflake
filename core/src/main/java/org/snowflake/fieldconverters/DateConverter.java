package org.snowflake.fieldconverters;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;

public class DateConverter implements FieldConverter {

    @Override
    public boolean accepts(Class<?> type) {
        return Date.class.equals(type);
    }

    @Override
    public Object convert(String input, Class<?> type) {
        if (StringUtils.isEmpty(input))
            return null;
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        try {
            return df.parse(input);
        } catch (ParseException e) {
            // should never happen, as validate should have caught this
            throw new RuntimeException(e);
        }
    }

}
