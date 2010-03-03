package org.snowflake.fieldconverters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.snowflake.fieldconverters.DateConverter;

public class DateConverterTest {

    @Test
    public void testConvert() {
        Object result = new DateConverter().convert("1/25/10", Date.class);
        assertTrue(result instanceof Date);

        Calendar convertedTime = Calendar.getInstance();
        convertedTime.setTime((Date) result);

        Calendar expectedTime = Calendar.getInstance();
        expectedTime.clear();
        expectedTime.set(Calendar.YEAR, 2010);
        expectedTime.set(Calendar.MONTH, 0);
        expectedTime.set(Calendar.DATE, 25);

        assertEquals(0, expectedTime.compareTo(convertedTime));
    }

}
