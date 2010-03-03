package org.snowflake.utils;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;


import org.junit.Test;
import org.snowflake.utils.HttpHelpers;

public class HttpHelpersTest {

    @Test
    public void testParseHttpParameters() throws UnsupportedEncodingException {
        String body = "Firstname=Testname&Lastname=%24person.Lastname&Birthday=%24person.Birthday";

        Map<String, String> map = HttpHelpers.parseHttpParameters(body, "UTF-8");
        assertEquals("Testname", map.get("Firstname"));
        assertEquals("$person.Lastname", map.get("Lastname"));
        assertEquals("$person.Birthday", map.get("Birthday"));
    }

}
