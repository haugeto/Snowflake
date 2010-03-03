package org.snowflake.utils;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.snowflake.utils.CollectionHelpers;


public class CollectionHelpersTest {

    @Test
    public void testCapitalizeKeys() {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put("key1", 1);
        map.put("key2", 2);
        map.put("key3", 3);
        CollectionHelpers.capitalizeKeys(map);
        assertEquals(new Integer(1), map.get("Key1"));
        assertEquals(new Integer(2), map.get("Key2"));
        assertEquals(new Integer(3), map.get("Key3"));
    }

}
